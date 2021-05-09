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

import com.github.kokorin.jaffree.JaffreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link StdReader} implementation which reads and ignores bytes read.
 *
 * @param <T>
 */
public class GobblingStdReader<T> implements StdReader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GobblingStdReader.class);
    private static final long REPORT_EVERY_BYTES = 1_000_000;
    private static final int BUFFER_SIZE = 1014;

    /**
     * Reads and ignores bytes read.
     *
     * @param stdOut input to read from
     * @return null
     */
    @Override
    public T read(final InputStream stdOut) {
        byte[] bytes = new byte[BUFFER_SIZE];
        int read = 0;
        long total = 0;
        long lastReport = 0;

        try {
            do {
                total += read;
                if (total - lastReport > REPORT_EVERY_BYTES) {
                    LOGGER.info("Read {} bytes", total);
                    lastReport = total;
                }
                read = stdOut.read(bytes);
            } while (read != -1);
        } catch (IOException e) {
            throw new JaffreeException("Failed to read input", e);
        } finally {
            LOGGER.info("Totally read {} bytes", total);
        }

        return null;
    }
}
