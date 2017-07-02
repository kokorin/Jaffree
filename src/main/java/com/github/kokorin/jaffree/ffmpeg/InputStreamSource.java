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

package com.github.kokorin.jaffree.ffmpeg;

import org.ebml.io.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InputStreamSource implements DataSource {
    private final InputStream inputStream;
    private long pointer = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamSource.class);

    public InputStreamSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public byte readByte() {
        try {
            byte result = (byte) inputStream.read();
            pointer++;
            return result;
        } catch (IOException e) {
            LOGGER.warn("Failed to read byte", e);
            return 0;
        }
    }

    @Override
    public int read(ByteBuffer byteBuffer) {
        int result = 0;
        try {
            while (byteBuffer.hasRemaining()) {
                int read = inputStream.read(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining());
                if (read == -1) {
                    break;
                }
                byteBuffer.position(byteBuffer.position() + read);
                result += read;
                pointer += read;
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to read bytebuffer", e);
            return 0;
        }

        return result;
    }

    @Override
    public long skip(long toSkip) {
        long result = 0;

        try {
            while (result < toSkip) {
                long skipped = inputStream.skip(toSkip - result);
                //TODO can it be?
                if (skipped == -1) {
                    break;
                }
                result += skipped;
                pointer += skipped;
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to skip bytes", e);
            return 0;
        }

        return result;
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
        throw new UnsupportedOperationException("Seek is not supported");
    }
}
