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

import com.github.kokorin.jaffree.JaffreeException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public abstract class SocketInput<T extends SocketInput<T>> extends BaseInput<T> implements Input {
    private final ServerSocket serverSocket;

    public SocketInput(String protocol) {
        this(protocol, "");
    }

    public SocketInput(String protocol, String suffix) {
        this.serverSocket = allocateSocket();
        super.setInput(protocol + "://127.0.0.1:" + serverSocket.getLocalPort() + suffix);
    }

    protected ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new JaffreeException("Failed to allocate socket", e);
        }
    }

    @Override
    public final Runnable helperThread() {
        final Negotiator negotiator = negotiator();

        return new Runnable() {
            @Override
            public void run() {
                try {
                    negotiator.negotiateAndClose(serverSocket);
                } catch (IOException e) {
                    throw new JaffreeException("Failed to negotiate via socket " + serverSocket, e);
                }
            }
        };
    }

    @Override
    public T setInput(String input) {
        throw new JaffreeException("SocketInput input can't be changed");
    }

    abstract Negotiator negotiator();

    interface Negotiator {
        /**
         * Negotiator <b>must</b> close passed in {@code ServerSocket}
         * @param serverSocket socket to communicate
         * @throws IOException
         */
        void negotiateAndClose(ServerSocket serverSocket) throws IOException;
    }
}
