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

import com.github.kokorin.jaffree.util.SocketOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TcpInput<T extends TcpInput<T>> extends SocketInput<T> implements Input {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpInput.class);

    public TcpInput() {
        super("tcp");
    }

    protected abstract Supplier supplier();

    @Override
    final Negotiator negotiator() {
        final Supplier supplier = supplier();

        return new Negotiator() {
            @Override
            public void negotiateAndClose(ServerSocket serverSocket) throws IOException {
                LOGGER.debug("Accepting connection: {}", serverSocket);
                Socket socket = serverSocket.accept();
                OutputStream outputStream = new SocketOutputStream(serverSocket, socket);
                LOGGER.debug("Passing output stream to supplier: {}", supplier);
                supplier.supplyAndClose(outputStream);
            }
        };
    }

    public interface Supplier {

        /**
         * Supplier <b>must</b> close passed {@link OutputStream} either in current or another thread.
         *
         * @param out OutputStream
         */
        void supplyAndClose(OutputStream out);
    }
}
