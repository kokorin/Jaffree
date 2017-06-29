package com.github.kokorin.jaffree.matroska;

import org.ebml.io.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InputStreamSource implements DataSource{
    private final BufferedInputStream inputStream;
    private long pointer = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamSource.class);

    public InputStreamSource(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
    }

    @Override
    public byte readByte() {
        try {
            return (byte) inputStream.read();
        } catch (IOException e) {
            LOGGER.warn("Failed to read byte", e);
            return 0;
        }
    }

    @Override
    public int read(ByteBuffer byteBuffer) {
        try {
            int result = 0;
            do {
                if (result > 0) {
                    LOGGER.warn("Failed to read bytes in single call: {}/{}", result, byteBuffer.limit());
                }
                int read = inputStream.read(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining());
                byteBuffer.position(byteBuffer.position() + read);
                result += read;
                pointer += read;
                LOGGER.info("Read {} bytes", read);
            } while (byteBuffer.remaining() > 0);
            return result;
        } catch (IOException e) {
            LOGGER.warn("Failed to read bytebuffer", e);
            return 0;
        }
    }

    @Override
    public long skip(long toSkip) {
        try {
            long skipped = 0;
            do {
                if (skipped > 0) {
                    LOGGER.warn("Failed to skip bytes in one call: {}/{}", skipped, toSkip);
                }
                skipped += inputStream.skip(toSkip - skipped);
                LOGGER.info("Skipped {} bytes", skipped);
            } while (skipped < toSkip);

            pointer += skipped;
            return skipped;
        } catch (IOException e) {
            LOGGER.warn("Failed to skip bytes", e);
            return 0;
        }
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public long getFilePointer() {
        return pointer;
    }

    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public long seek(long l) {
        throw new UnsupportedOperationException("Not supported");
    }
}
