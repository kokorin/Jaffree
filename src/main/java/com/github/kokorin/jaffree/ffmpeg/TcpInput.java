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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

public abstract class TcpInput<T extends TcpInput<T>> extends BaseInput<T> implements Input {
    private final ServerSocket serverSocket;

    public TcpInput() {
        this.serverSocket = allocateSocket();
        setInput("tcp://127.0.0.1:" + serverSocket.getLocalPort());
    }

    protected ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

    @Override
    public final Runnable helperThread() {
        final Supplier supplier = supplier();

        return new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = new SocketOutputStream(serverSocket);
                    supplier.supplyAndClose(outputStream);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read from socket " + serverSocket, e);
                }
            }
        };
    }

    protected abstract Supplier supplier();

    public interface Supplier {

        /**
         * Supplier <b>must</b> close passed {@link OutputStream} either in current or another thread.
         * @param out OutputStream
         */
        void supplyAndClose(OutputStream out);
    }
}
