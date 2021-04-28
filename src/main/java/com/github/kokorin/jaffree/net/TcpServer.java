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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.ffmpeg.Input;
import com.github.kokorin.jaffree.ffmpeg.Output;
import com.github.kokorin.jaffree.process.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract TCP Server implementing {@link ProcessHelper}.
 * <p>
 * This class is intended to be used in different ffmpeg {@link Input} &amp; {@link Output}
 * implementations which interact with ffmpeg via TCP sockets.
 */
public abstract class TcpServer implements ProcessHelper {
    private final ServerSocket serverSocket;
    private final String addressAndPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);

    /**
     * Creates TCP server.
     * <p>
     * This implementation is not intended to be used anywhere except this project.
     * It servers only the first accepted connection in single thread.
     *
     * @param serverSocket server socket to accept connection.
     *                     Pay attention that server socket should listen on <b>loopback</b>
     *                     (127.0.0.1) address for security reasons.
     */
    protected TcpServer(final ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.addressAndPort = serverSocket.getInetAddress().getHostAddress() + ":"
                + serverSocket.getLocalPort();
    }

    @Override
    public final void run() {
        try (Closeable toClose = serverSocket;
             Socket socket = serverSocket.accept()) {
            LOGGER.debug("Connection accepted, serving: {}", getAddressAndPort());
            serve(socket);
            LOGGER.debug("Served successfully: {}", getAddressAndPort());
        } catch (Exception e) {
            throw new JaffreeException("TCP negotiation failed", e);
        }
    }

    /**
     * Serves TCP connection.
     *
     * @param socket TCP socket
     * @throws IOException socket IO exception
     */
    protected abstract void serve(Socket socket) throws IOException;

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /**
     * Closes underlying TCP Socket.
     *
     * @throws IOException if any IO error
     */
    @Override
    public void close() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    /**
     * Returns string representation of host and port.
     *
     * @return host and port string
     */
    public String getAddressAndPort() {
        return addressAndPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TcpServer{addressAndPort=" + addressAndPort + '}';
    }

    /**
     * Allocates {@link ServerSocket} on random port of loopback network interface.
     *
     * @return ServerSocket
     */
    protected static ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new JaffreeException("Failed to allocate socket", e);
        }
    }

}
