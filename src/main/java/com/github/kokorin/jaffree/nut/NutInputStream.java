package com.github.kokorin.jaffree.nut;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class NutInputStream implements AutoCloseable {

    private final InputStream input;
    private long position = 0;

    public NutInputStream(InputStream input) {
        if (!(input instanceof BufferedInputStream)) {
            input = new BufferedInputStream(input);
        }

        this.input = input;
    }

    public long getPosition() {
        return position;
    }

    /**
     * v   (variable length value, unsigned)
     *
     * @return unsigned value
     */
    public long readValue() throws Exception {
        long result = 0;

        while (input.available() > 0) {
            int tmp = input.read();
            position++;

            boolean hasMore = (tmp & 0x80) > 0;
            if (hasMore)
                result = (result << 7) + tmp - 0x80;
            else
                return (result << 7) + tmp;
        }

        return -1;
    }

    /**
     * s   (variable length value, signed)
     *
     * @return signed value
     */
    public long readSignedValue() throws Exception {
        long tmp = readValue();
        tmp++;
        if ((tmp & 1) > 0) {
            return -(tmp >> 1);
        }

        return tmp >> 1;
    }

    /**
     * f(n)    (n fixed bits in big-endian order)
     * n == 64
     *
     * @return long
     */
    public long readLong() throws Exception {
        long result = 0;

        for (int i = 0; i < 8; i++) {
            result = (result << 8) + input.read();
            position++;
        }

        return result;
    }

    /**
     * u(n)    (unsigned number encoded in n bits in MSB-first order)
     * n == 32
     *
     * @return int as long
     */
    long readInt() throws Exception {
        long result = 0;

        for (int i = 0; i < 4; i++) {
            result = (result << 8) + input.read();
            position++;
        }

        return result;
    }

    /**
     * f(n)    (n fixed bits in big-endian order)
     * n == 8
     *
     * @return byte
     */
    public int readByte() throws Exception {
        int result = input.read();
        position++;

        return result;
    }

    /**
     * vb  (variable length binary data or string)
     *
     * @return String
     */
    public String readVariableString() throws Exception {
        byte[] bytes = readVariableBytes();
        return toString(bytes);
    }

    /**
     * Reads input till char \0 not found
     */
    public String readCString() throws Exception {
        byte[] buffer = new byte[1024];
        int length = 0;

        int b;
        while ((b = input.read()) != 0) {
            buffer[length] = (byte) b;
            length++;
        }

        return toString(buffer, length);
    }

    /**
     * vb  (variable length binary data or string)
     *
     * @return String
     */
    public byte[] readVariableBytes() throws Exception {
        int length = (int) readValue();
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            result[i] = (byte) input.read();
            position++;
        }

        return result;
    }

    /**
     * Returns next byte, which will be read with any read*() method
     *
     * @return next byte
     */
    public byte checkNextByte() throws Exception {
        input.mark(1);
        byte result = (byte) input.read();
        input.reset();

        return result;
    }

    /**
     * Returns true if stream contains more data
     *
     * @return next byte
     */
    public boolean hasMoreData() throws Exception {
        input.mark(1);
        int result = input.read();
        input.reset();

        return result != -1;
    }

    public byte[] readBytes(long toRead) throws Exception {
        byte[] result = new byte[(int) toRead];
        int start = 0;

        while (start < toRead) {
            long read = input.read(result, start, (int) toRead - start);
            position += read;
            start += read;
        }

        return result;
    }

    public void skipBytes(long toSkip) throws Exception {
        while (toSkip > 0) {
            long skipped = input.skip(toSkip);
            position += skipped;
            toSkip -= skipped;
        }
    }

    @Override
    public void close() throws Exception {
        input.close();
    }

    private static String toString(byte[] bytes) {
        return toString(bytes, bytes.length);
    }

    private static String toString(byte[] bytes, int length) {
        char[] result = new char[length];

        for (int i = 0; i < length; i++) {
            result[i] = (char) bytes[i];
        }

        return String.valueOf(result);
    }
}
