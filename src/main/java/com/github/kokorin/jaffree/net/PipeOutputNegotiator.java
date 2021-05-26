/*
 *    Copyright  2021 Denis Kokorin
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

package com.github.kokorin.jaffree.net;

import com.github.kokorin.jaffree.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * {@link TcpNegotiator} implementation which copies bytes from {@link Socket}
 * to {@link InputStream}.
 */
public class PipeOutputNegotiator implements TcpNegotiator {
    private final OutputStream destination;
    private final int bufferSize;

    /**
     * Creates {@link PipeOutputNegotiator}.
     *
     * @param destination output stream to copy to
     * @param bufferSize  buffer size to copy data
     */
    public PipeOutputNegotiator(final OutputStream destination, final int bufferSize) {
        this.destination = destination;
        this.bufferSize = bufferSize;
    }

    /**
     * Copies bytes from socket {@link OutputStream} to {@link #destination}.
     *
     * @param socket TCP socket
     * @throws IOException if any IO error
     */
    @Override
    public void negotiate(final Socket socket) throws IOException {
        try (InputStream source = socket.getInputStream()) {
            IOUtil.copy(source, destination, bufferSize);
        }
    }
}
