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

import org.ebml.io.DataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class OutputStreamWriter implements DataWriter {
    private final OutputStream outputStream;
    private final WritableByteChannel channel;
    private long pointer;

    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamSource.class);

    public OutputStreamWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
        //TODO close channel
        this.channel = Channels.newChannel(outputStream);
    }

    @Override
    public int write(byte b) {
        int result = 0;

        try {
            outputStream.write(b);
            pointer++;
            result = 1;
        } catch (IOException e) {
            LOGGER.warn("Failed to write byte", e);
        }

        return result;
    }

    @Override
    public int write(ByteBuffer buff) {
        int result = 0;

        try {
            while (buff.hasRemaining()) {
                result += channel.write(buff);
            }
            pointer += result;
        } catch (IOException e) {
            LOGGER.warn("Failed to write bytes", e);
        }

        return result;
    }

    @Override
    public long length() {
        return -1;
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
    public long seek(long pos) {
       throw new UnsupportedOperationException("Seek is not supported");
    }
}
