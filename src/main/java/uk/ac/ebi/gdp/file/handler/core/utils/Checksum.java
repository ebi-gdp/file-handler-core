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
package uk.ac.ebi.gdp.file.handler.core.utils;

import uk.ac.ebi.gdp.file.handler.core.exception.ServerException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.getInstance;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public interface Checksum {
    static MessageDigest getMD5MessageDigest() {
        try {
            return getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaException) {
            throw new ServerException("Error while getting MessageDigest instance");
        }
    }

    static String normalize(final MessageDigest messageDigest) {
        return printHexBinary(messageDigest.digest()).toLowerCase();
    }
}
