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

import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@ThreadSafe
public class NegotiatingTcpServer extends TcpServer {
    protected final TcpNegotiator negotiator;

    protected NegotiatingTcpServer(ServerSocket serverSocket, TcpNegotiator negotiator) {
        super(serverSocket);
        this.negotiator = negotiator;
    }

    @Override
    protected void serve(Socket socket) throws IOException {
        negotiator.negotiate(socket);
    }

    public static NegotiatingTcpServer onRandomPort(TcpNegotiator negotiator) {
        return new NegotiatingTcpServer(allocateSocket(), negotiator);
    }

}
