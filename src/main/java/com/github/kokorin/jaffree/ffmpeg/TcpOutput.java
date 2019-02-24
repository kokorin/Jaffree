package com.github.kokorin.jaffree.ffmpeg;
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

import com.github.kokorin.jaffree.util.SocketInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

public abstract class TcpOutput<T extends TcpOutput<T>> extends BaseOutput<T> implements Output {
    private final ServerSocket serverSocket;

    public TcpOutput() {
        this.serverSocket = allocateSocket();

        setOutput("tcp://127.0.0.1:" + serverSocket.getLocalPort()/* + "?timeout=1000000"*/);
    }

    @Override
    public final Runnable helperThread() {
        final Consumer consumer = consumer();

        if (consumer == null) {
            return null;
        }
        return new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = new SocketInputStream(serverSocket);
                    consumer.consumeAndClose(inputStream);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read from socket " + serverSocket, e);
                }
            }
        };
    }

    protected abstract Consumer consumer();

    protected ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
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
