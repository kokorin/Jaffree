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

import com.github.kokorin.jaffree.net.SocketInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provides possibility to consume ffmpeg output via TCP socket.
 * <b>Note</b> there are limitation because of non-seekable nature of TCP output.
 *
 * @param <T>
 */
public abstract class TcpOutput<T extends TcpOutput<T>> extends SocketOutput<T> implements Output {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpOutput.class);

    public TcpOutput() {
        super("tcp");
    }

    protected abstract Consumer consumer();

    @Override
    Negotiator negotiator() {
        final Consumer consumer = consumer();

        return new Negotiator() {
            @Override
            public void negotiateAndClose(ServerSocket serverSocket) throws IOException {
                LOGGER.debug("Accepting connection: {}", serverSocket);
                Socket socket = serverSocket.accept();
                InputStream inputStream = new SocketInputStream(serverSocket, socket);
                LOGGER.debug("Passing output stream to consumer: {}", consumer);
                consumer.consumeAndClose(inputStream);
            }
        };
    }

    public interface Consumer {

        /**
         * Consumer <b>must</b> close passed {@link InputStream} either in current or another thread.
         *
         * @param in InputStream
         */
        void consumeAndClose(InputStream in);
    }
}
