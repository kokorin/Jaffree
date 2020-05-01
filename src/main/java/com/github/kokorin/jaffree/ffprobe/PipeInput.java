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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.util.IOUtil;
import com.github.kokorin.jaffree.util.SocketOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PipeInput extends SocketInput {
    private final InputStream inputStream;
    private final int bufferSize;

    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeInput.class);

    public PipeInput(InputStream inputStream) {
        this(inputStream, DEFAULT_BUFFER_SIZE);
    }

    public PipeInput(InputStream inputStream, int bufferSize) {
        super("tcp");
        this.inputStream = inputStream;
        this.bufferSize = bufferSize;
    }

    @Override
    Negotiator negotiator() {
        return new Negotiator() {
            @Override
            public void negotiateAndClose(ServerSocket serverSocket) throws IOException {
                LOGGER.debug("Accepting connection: {}", serverSocket);

                try (Socket socket = serverSocket.accept();
                     OutputStream outputStream = new SocketOutputStream(serverSocket, socket)) {
                    LOGGER.debug("Connection accepted, copying");
                    IOUtil.copy(inputStream, outputStream, bufferSize);
                } catch (SocketException e) {
                    // client has no way to notify server that no more data is needed
                    LOGGER.debug("Ignoring exception: " + e.getMessage());
                }
            }
        };
    }
}
