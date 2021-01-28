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

package com.github.kokorin.jaffree.nut;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link NutInputStream} adapts {@link InputStream} and provides methods to read
 * Nut-specific data structures.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class NutInputStream implements AutoCloseable {

    private final InputStream input;
    private long position = 0;

    /**
     * Creates {@link NutInputStream}.
     *
     * @param input input stream
     */
    public NutInputStream(final InputStream input) {
        BufferedInputStream bufferedInputStream;

        if (input instanceof BufferedInputStream) {
            bufferedInputStream = (BufferedInputStream) input;
        } else {
            bufferedInputStream = new BufferedInputStream(input);
        }

        this.input = bufferedInputStream;
    }

    /**
     * @return current position
     */
    public long getPosition() {
        return position;
    }

    /**
     * Reads v type (variable length value, unsigned).
     *
     * @return unsigned value, or -1 if no data
     */
    public long readValue() throws IOException {
        long result = 0;
        boolean hasMore = true;

        while (hasMore) {
            int read = input.read();
            if (read == -1) {
                throw new EOFException("No more data");
            }

            hasMore = (read & 0x80) > 0;
            result = (result << 7) + (read & 0x7F);
            position++;
        }

        return result;
    }

    /**
     * Reads s type (variable length value, signed).
     *
     * @return signed value
     */
    public long readSignedValue() throws IOException {
        long tmp = readValue();
        tmp++;
        if ((tmp & 1) > 0) {
            return -(tmp >> 1);
        }

        return tmp >> 1;
    }

    /**
     * Reads f(n) type (n fixed bits in big-endian order).
     * n == 64
     *
     * @return long
     */
    public long readLong() throws IOException {
        long result = 0;

        for (int i = 0; i < 8; i++) {
            int read = input.read();
            if (read == -1) {
                throw new EOFException("No more data");
            }
            result = (result << 8) + read;
            position++;
        }

        return result;
    }

    /**
     * Reads u(n) type (unsigned number encoded in n bits in MSB-first order).
     * n == 32
     *
     * @return int as long
     */
    long readInt() throws IOException {
        long result = 0;

        for (int i = 0; i < 4; i++) {
            int read = input.read();
            if (read == -1) {
                throw new EOFException("No more data");
            }
            result = (result << 8) + read;
            position++;
        }

        return result;
    }

    /**
     * Reads f(n) type (n fixed bits in big-endian order).
     * n == 8
     *
     * @return byte
     */
    public int readByte() throws IOException {
        int result = input.read();
        if (result == -1) {
            throw new EOFException("No more data");
        }
        position++;

        return result;
    }

    /**
     * Reads vb type (variable length binary data or string).
     *
     * @return String
     */
    public String readVariableString() throws IOException {
        byte[] bytes = readVariableBytes();
        return new String(bytes);
    }

    /**
     * Reads input till char \0 not found.
     *
     * @return String
     */
    public String readCString() throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(32)) {
            while (true) {
                int read = input.read();
                if (read == -1) {
                    throw new EOFException("No more data");
                }
                position++;

                if (read == 0) {
                    break;
                }
                buffer.write(read);
            }

            return buffer.toString();
        }
    }

    /**
     * Reads vb type (variable length binary data or string).
     *
     * @return String
     */
    public byte[] readVariableBytes() throws IOException {
        int length = (int) readValue();
        return readBytes(length);
    }

    /**
     * Reads t type (v coded universal timestamp).
     *
     * @param timeBaseCount time base count
     * @return Timestamp
     */
    public Timestamp readTimestamp(final int timeBaseCount) throws IOException {
        long tmp = readValue();
        int timebaseId = (int) (tmp % timeBaseCount);
        long pts = tmp / timeBaseCount;

        return new Timestamp(timebaseId, pts);
    }

    /**
     * Returns next byte (if available), which will be read with any read*() method.
     * <p>
     * Note: position in {@link InputStream} isn't changed.
     *
     * @return next byte, or -1 if the end of the stream is reached
     */
    public int checkNextByte() throws IOException {
        input.mark(1);
        int result = input.read();
        input.reset();

        return result;
    }

    /**
     * Returns true if stream contains more data.
     *
     * @return next byte
     */
    public boolean hasMoreData() throws IOException {
        input.mark(1);
        int result = input.read();
        input.reset();

        return result != -1;
    }

    /**
     * Reads specified number of bytes.
     *
     * @param toRead bytes to read
     * @return byte array
     */
    public byte[] readBytes(final long toRead) throws IOException {
        byte[] result = new byte[(int) toRead];
        int start = 0;

        while (start < toRead) {
            long read = input.read(result, start, (int) toRead - start);
            if (read == -1) {
                throw new EOFException("No more data");
            }

            position += read;
            start += read;
        }

        return result;
    }

    /**
     * Skips specified number of bytes.
     *
     * @param toSkip bytes to skip
     */
    public void skipBytes(final long toSkip) throws IOException {
        long leftToSkip = toSkip;
        while (leftToSkip > 0) {
            long skipped = input.skip(leftToSkip);
            if (skipped == 0) {
                // if no bytes were skipped - it possibly means that input is depleted or closed
                // read one byte to make sure
                int read = input.read();
                if (read == -1) {
                    throw new EOFException("No more data");
                }
                skipped = 1;
            }
            position += skipped;
            leftToSkip -= skipped;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
}
