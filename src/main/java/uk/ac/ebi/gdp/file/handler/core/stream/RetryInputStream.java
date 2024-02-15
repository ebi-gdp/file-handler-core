/*
 *
 * Copyright 2021 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.gdp.file.handler.core.stream;

import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.springframework.core.io.buffer.DataBufferUtils.releaseConsumer;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.reactive.function.BodyExtractors.toDataBuffers;
import static uk.ac.ebi.gdp.file.handler.core.exception.ExceptionHandler.throwClientError;
import static uk.ac.ebi.gdp.file.handler.core.exception.ExceptionHandler.throwServerError;
import static uk.ac.ebi.gdp.file.handler.core.exception.ServerException.serverException;

public class RetryInputStream extends CountingInputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryInputStream.class);

    private final WebClient webClient;
    private final RetryTemplate retryTemplate;
    private final Path requestPath;
    private final long startRange;
    private final long endRange;
    private final int pipeSize;

    /**
     * Constructs a new CountingInputStream.
     *
     * @param webClient execute download request
     * @param retryTemplate retry download read operation
     * @param requestPath download request path
     * @param startRange download start range
     * @param endRange download end range
     * @param pipeSize data buffer input stream pipe size
     */
    public RetryInputStream(final WebClient webClient,
                            final RetryTemplate retryTemplate,
                            final Path requestPath,
                            final long startRange,
                            final long endRange,
                            final int pipeSize) throws IOException {
        super(null);
        this.webClient = webClient;
        this.retryTemplate = retryTemplate;
        this.requestPath = requestPath;
        this.startRange = startRange;
        this.endRange = endRange;
        this.pipeSize = pipeSize;
        initializeInputStream();
    }

    @Override
    public int read() throws IOException {
        try {
            return super.read();
        } catch (IOException ioException) {
            logError(ioException);
            initializeInputStream();
            return super.read();
        }
    }

    @Override
    public int read(final byte[] bts) throws IOException {
        try {
            return super.read(bts);
        } catch (IOException ioException) {
            logError(ioException);
            initializeInputStream();
            return super.read(bts);
        }
    }

    @Override
    public int read(final byte[] bts,
                    final int off,
                    final int len) throws IOException {
        try {
            return super.read(bts, off, len);
        } catch (IOException ioException) {
            logError(ioException);
            initializeInputStream();
            return super.read(bts, off, len);
        }
    }

    private void initializeInputStream() throws IOException {
        closeQuietly(this.in);
        this.in = getInputStream();
    }

    private InputStream getInputStream() throws IOException {
        return retryTemplate.execute(context -> {
            final PipedOutputStream osPipe = new PipedOutputStream();
            final PipedInputStream isPipe = new PipedInputStream(osPipe, pipeSize);
            final ResponseEntity<Flux<DataBuffer>> dataBufferResponse = executeDownloadRequest();
            final Flux<DataBuffer> dataBuffer = dataBufferResponse.getBody();

            if (dataBuffer == null) {
                throw serverException(INTERNAL_SERVER_ERROR.value(), "Response body is null");
            }

            DataBufferUtils
                    .write(dataBuffer, osPipe)
                    .doOnComplete(() -> closeQuietly(osPipe))
                    .subscribe(releaseConsumer());
            return isPipe;
        });
    }

    private ResponseEntity<Flux<DataBuffer>> executeDownloadRequest() {
        return webClient
                .get()
                .uri(requestPath.toString())
                .header("Range", String.format("bytes=%d-%d", startRange + this.getByteCount(), endRange))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, throwClientError())
                .onStatus(HttpStatus::is5xxServerError, throwServerError())
                .toEntityFlux(toDataBuffers())
                .block();
    }

    private void logError(final IOException ioException) {
        LOGGER.error("Error occurred while reading bytes: " + ioException.getMessage(), ioException);
    }
}
