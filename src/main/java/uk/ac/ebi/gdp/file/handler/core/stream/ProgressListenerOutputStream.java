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

import org.apache.commons.io.output.CountingOutputStream;
import uk.ac.ebi.gdp.file.handler.core.listener.ProgressListener;

import java.io.IOException;
import java.io.OutputStream;

public class ProgressListenerOutputStream extends CountingOutputStream {

    private final ProgressListener progressListener;

    /**
     * Constructs a new CountingOutputStream.
     *
     * @param outputStream the OutputStream to write to
     */
    public ProgressListenerOutputStream(final OutputStream outputStream,
                                        final ProgressListener progressListener) {
        super(outputStream);
        this.progressListener = progressListener;
    }

    @Override
    public void write(final byte[] b) throws IOException {
        super.write(b);
        updateProgressListener();
    }

    @Override
    public void write(final byte[] b,
                      final int off,
                      final int len) throws IOException {
        super.write(b, off, len);
        updateProgressListener();
    }

    @Override
    public void write(final int b) throws IOException {
        super.write(b);
        updateProgressListener();
    }

    private void updateProgressListener() {
        progressListener.progress(getByteCount());
    }
}
