/*
 *    Copyright 2017-2021 Denis Kokorin
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link NutOutputStream} implements core NUT write operations.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class NutOutputStream implements AutoCloseable {
    private final OutputStream output;
    private final CRC32 crc32 = new CRC32();
    private long position = 0;

    /**
     * Creates {@link NutOutputStream}.
     *
     * @param output output stream
     */
    public NutOutputStream(final OutputStream output) {
        this.output = asBuffered(output);
    }

    /**
     * Writes v type (variable length value, unsigned).
     *
     * @param value value
     * @throws IOException if any IO error
     */
    public void writeValue(final long value) throws IOException {
        long value63bits = value & 0x7FFFFFFFFFFFFFFFL;
        int i;
        for (i = 7; ; i += 7) {
            if (value63bits >> i == 0) {
                break;
            }
        }

        for (i -= 7; i > 0; i -= 7) {
            int b = (int) (0x80 | (value63bits >> i));
            output.write(b);
            crc32.update(b);
            position++;
        }

        int b = (int) (value63bits & 0x7F);
        output.write(b);
        crc32.update(b);
        position++;
    }

    /**
     * Writes s type (variable length value, signed).
     *
     * @param signed signed value
     * @throws IOException if any IO error
     */
    public void writeSignedValue(final long signed) throws IOException {
        long value;
        if (signed > 0) {
            value = signed << 1;
        } else {
            value = 1 - (signed << 1);
        }
        value--;
        writeValue(value);
    }

    /**
     * Writes f(n) type (n fixed bits in big-endian order).
     * n == 64
     *
     * @param value value
     * @throws IOException if any IO error
     */
    public void writeLong(final long value) throws IOException {
        for (int i = 7; i >= 0; i--) {
            int b = (int) ((value >> (8 * i)) & 0xFF);
            output.write(b);
            crc32.update(b);
            position++;
        }
    }

    /**
     * Writes f(n) type (n fixed bits in big-endian order).
     * n == 32
     *
     * @param value value
     * @throws IOException if any IO error
     */
    public void writeInt(final long value) throws IOException {
        for (int i = 3; i >= 0; i--) {
            int b = (int) ((value >> (8 * i)) & 0xFF);
            output.write(b);
            crc32.update(b);
            position++;
        }
    }

    /**
     * Writes f(n) type (n fixed bits in big-endian order).
     * n == 8
     *
     * @param value value
     * @throws IOException if any IO error
     */
    public void writeByte(final int value) throws IOException {
        output.write(value);
        crc32.update(value);
        position++;
    }

    /**
     * Writes vb type (variable length binary data or string).
     *
     * @param data string data
     * @throws IOException if any IO error
     */
    public void writeVariablesString(final String data) throws IOException {
        writeVariableBytes(data.getBytes());
    }

    /**
     * Writes null-terminated string.
     *
     * @param data string data
     * @throws IOException if any IO error
     */
    public void writeCString(final String data) throws IOException {
        writeBytes(data.getBytes());
        writeByte(0);
    }

    /**
     * Writes vb type (variable length binary data or string).
     *
     * @param data bytes
     * @throws IOException if any IO error
     */
    public void writeVariableBytes(final byte[] data) throws IOException {
        writeValue(data.length);
        writeBytes(data);
    }

    /**
     * Writes t type (v coded universal timestamp).
     *
     * @param timeBaseCount time base count
     * @param timestamp     timestamp
     * @throws IOException if any IO error
     */
    public void writeTimestamp(final int timeBaseCount,
                               final Timestamp timestamp) throws IOException {
        long value = timestamp.pts * timeBaseCount + timestamp.timebaseId;
        writeValue(value);
    }

    /**
     * Writes raw bytes.
     *
     * @param data bytes
     * @throws IOException if any IO error
     */
    public void writeBytes(final byte[] data) throws IOException {
        output.write(data);
        crc32.update(data);
        position += data.length;
    }

    /**
     * Resets NUT CRC32.
     */
    public void resetCrc32() {
        crc32.reset();
    }

    /**
     * Writes NUT CRC32.
     *
     * @throws IOException if any IO error
     */
    public void writeCrc32() throws IOException {
        writeInt(crc32.getValue());
    }

    /**
     * Returns current position (offset) in bytes.
     *
     * @return current position
     */
    public long getPosition() {
        return position;
    }

    /**
     * Flushes underlying {@link OutputStream}.
     *
     * @throws IOException if any IO error
     */
    public void flush() throws IOException {
        output.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        output.close();
    }

    private static BufferedOutputStream asBuffered(final OutputStream outputStream) {
        if (outputStream instanceof BufferedOutputStream) {
            return (BufferedOutputStream) outputStream;
        }
        return new BufferedOutputStream(outputStream);
    }
}
