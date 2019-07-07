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

package com.github.kokorin.jaffree.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketOutputStream extends OutputStream {
    private final ServerSocket serverSocket;
    private final Socket socket;
    private final OutputStream outputStream;

    public SocketOutputStream(ServerSocket serverSocket, Socket socket) throws IOException {
        this.serverSocket = serverSocket;
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        try (ServerSocket toCloseServerSocket = serverSocket;
             Socket toCloseSocket = socket;
             OutputStream toCloseOutputStream = outputStream) {

        }
    }
}
