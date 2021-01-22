/*
 *    Copyright  2019-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.util.IOUtil;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * {@link Input} implementation which passes {@link InputStream} to ffmpeg as input.
 *
 * @see ChannelInput
 */
public class PipeInput extends TcpInput<PipeInput> implements Input {
    private final PipeInputNegotiator negotiator;

    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeInput.class);

    public PipeInput(InputStream source) {
        this(new PipeInputNegotiator(source));
    }

    public PipeInput(PipeInputNegotiator negotiator) {
        super(negotiator);
        this.negotiator = negotiator;
    }

    public PipeInput setBufferSize(int bufferSize) {
        negotiator.setBufferSize(bufferSize);
        return this;
    }

    public static PipeInput pumpFrom(InputStream source) {
        return new PipeInput(source);
    }

    public static PipeInput pumpFrom(InputStream source, int bufferSize) {
        return pumpFrom(source)
                .setBufferSize(bufferSize);
    }

    @ThreadSafe
    protected static class PipeInputNegotiator implements TcpNegotiator {
        private final InputStream source;
        @GuardedBy("this")
        private int bufferSize = DEFAULT_BUFFER_SIZE;

        public PipeInputNegotiator(InputStream source) {
            this.source = source;
        }

        public synchronized void setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public synchronized void negotiate(Socket socket) throws IOException {
            try (OutputStream destination = socket.getOutputStream()) {
                IOUtil.copy(source, destination, bufferSize);
            } catch (SocketException e) {
                // Client (ffmpeg) has no way to notify server that no more data is needed.
                // It just closes TCP connection on its side.
                LOGGER.debug("Ignoring exception: " + e.getMessage());
            }
        }
    }
}
