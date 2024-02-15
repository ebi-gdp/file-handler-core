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
package uk.ac.ebi.gdp.file.handler.core.properties;

public class WebClientProperties {
    private int pipeSize;
    private int connectionTimeout;
    private int socketTimeout;
    private int readWriteTimeout;

    public WebClientProperties() {
    }

    public int getPipeSize() {
        return pipeSize;
    }

    public void setPipeSize(final int pipeSize) {
        this.pipeSize = pipeSize;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getReadWriteTimeout() {
        return readWriteTimeout;
    }

    public void setReadWriteTimeout(final int readWriteTimeout) {
        this.readWriteTimeout = readWriteTimeout;
    }
}
