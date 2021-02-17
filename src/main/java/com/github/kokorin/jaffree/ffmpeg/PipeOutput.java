/*
 *    Copyright  2019 Denis Kokorin
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

import com.github.kokorin.jaffree.JaffreeRuntimeException;
import com.github.kokorin.jaffree.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides possibility to consume ffmpeg output via TCP socket.
 * <p>
 * <b>Note</b> there are limitations because of non-seekable nature of TCP output.
 */
public class PipeOutput extends TcpOutput<PipeOutput> implements Output {
    private final Consumer consumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(PipeOutput.class);

    public PipeOutput(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected Consumer consumer() {
        LOGGER.warn("It's recommended to use ChannelOutput since ffmpeg requires seekable output for many formats");
        return consumer;
    }

    public static PipeOutput withConsumer(Consumer consumer) {
        return new PipeOutput(consumer);
    }

    public static PipeOutput pumpTo(OutputStream destination) {
        return pumpTo(destination, 1_000_000);
    }

    public static PipeOutput pumpTo(OutputStream destination, int bufferSize) {
        return new PipeOutput(new PipeConsumer(destination, bufferSize));
    }

    private static class PipeConsumer implements Consumer {
        private final OutputStream destination;
        private final int bufferSize;

        public PipeConsumer(OutputStream destination, int bufferSize) {
            this.destination = destination;
            this.bufferSize = bufferSize;
        }

        @Override
        public void consumeAndClose(InputStream source) {
            try (Closeable toClose = source) {
                IOUtil.copy(source, destination, bufferSize);
            } catch (IOException e) {
                throw new JaffreeRuntimeException("Failed to copy data", e);
            }
        }
    }
}
