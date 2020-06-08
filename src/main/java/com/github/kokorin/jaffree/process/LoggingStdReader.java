/*
 *    Copyright  2017 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link StdReader} implementation which reads and logs everything been read.
 *
 * @param <T>
 */
public class LoggingStdReader<T> implements StdReader<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStdReader.class);

    /**
     * @param stdOut stream to read from
     * @return null
     */
    @Override
    public T read(final InputStream stdOut) {
        // TODO use line iterator?
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));

        try {
            String line;
            // TODO log message with the same logging level
            // for example if message starts with [DEBUG] output it to debug.
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stdout (stderr)", e);
        }

        return null;
    }
}
