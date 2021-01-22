/*
 *    Copyright  2019 Apache commons-io participants, Denis Kokorin
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

package com.github.kokorin.jaffree.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple IO utils.
 * <p>
 * Kindly borrowed from commons-io.
 */
public final class IOUtil {
    private IOUtil() {
    }

    public static final int EOF = -1;

    /**
     * Copies everything form input to output.
     *
     * @param input      input stream
     * @param output     output stream
     * @param bufferSize buffer size to use
     * @return bytes been copied
     * @throws IOException Stream IO exception
     */
    public static long copy(final InputStream input, final OutputStream output,
                            final int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive");
        }

        return copy(input, output, new byte[bufferSize]);
    }

    /**
     * Copies everything form input to output.
     *
     * @param input  input stream
     * @param output output stream
     * @param buffer buffer to use
     * @return bytes been copied
     * @throws IOException Stream IO exception
     */
    public static long copy(final InputStream input, final OutputStream output, final byte[] buffer)
            throws IOException {
        if (buffer.length == 0) {
            throw new IllegalArgumentException("Buffer  must be not empty");
        }

        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}

