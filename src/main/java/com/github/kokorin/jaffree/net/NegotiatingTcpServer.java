/*
 *    Copyright 2021 Denis Kokorin
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * {@link TcpServer} implementation using {@link TcpNegotiator} to serve TCP connection.
 */
public class NegotiatingTcpServer extends TcpServer {
    private final TcpNegotiator negotiator;

    protected NegotiatingTcpServer(final ServerSocket serverSocket,
                                   final TcpNegotiator negotiator) {
        super(serverSocket);
        this.negotiator = negotiator;
    }

    /**
     * Serves TCP connection using {@link TcpNegotiator}.
     *
     * @param socket TCP socket
     * @throws IOException socket IO exception
     */
    @Override
    protected void serve(final Socket socket) throws IOException {
        negotiator.negotiate(socket);
    }

    /**
     * Creates {@link NegotiatingTcpServer} waiting for TCP connection on random port.
     *
     * @param negotiator negotiator to use
     * @return NegotiatingTcpServer
     */
    public static NegotiatingTcpServer onRandomPort(final TcpNegotiator negotiator) {
        return new NegotiatingTcpServer(allocateSocket(), negotiator);
    }

}
