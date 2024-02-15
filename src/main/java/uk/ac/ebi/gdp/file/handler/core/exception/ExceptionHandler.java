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
package uk.ac.ebi.gdp.file.handler.core.exception;

import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static reactor.core.publisher.Mono.error;
import static uk.ac.ebi.gdp.file.handler.core.exception.ClientException.clientException;
import static uk.ac.ebi.gdp.file.handler.core.exception.ServerException.serverException;

public interface ExceptionHandler {
    static Function<ClientResponse, Mono<? extends Throwable>> throwClientError() {
        return clientResponse -> clientResponse
                .createException()
                .flatMap(transformer -> error(
                        clientException(
                                clientResponse.statusCode().value(),
                                transformer.getResponseBodyAsString()
                        )
                ));
    }

    static Function<ClientResponse, Mono<? extends Throwable>> throwServerError() {
        return clientResponse -> clientResponse
                .createException()
                .flatMap(transformer -> error(
                        serverException(
                                clientResponse.statusCode().value(),
                                transformer.getResponseBodyAsString()
                        )
                ));
    }
}
