package com.github.kokorin.jaffree.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TcpServer implements Runnable {
    // TODO allocate socket in run() method - would require to run Runnables before
    //  command-line construction
    private final ServerSocket serverSocket = allocateSocket();

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);

    @Override
    public final void run() {
        try (Closeable toClose = serverSocket;
             Socket socket = serverSocket.accept()) {
            LOGGER.debug("Connection accepted, serving: {}", getAddressAndPort());
            serve(socket);
            LOGGER.debug("Served successfully: {}", getAddressAndPort());
        } catch (Exception e) {
            throw new RuntimeException("TCP negotiation failed", e);
        }
    }

    protected abstract void serve(Socket socket) throws IOException;

    // TODO introduce property addressAndPort
    public String getAddressAndPort() {
        return "127.0.0.1:" + serverSocket.getLocalPort();
    }

    protected ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

}
