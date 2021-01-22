package com.github.kokorin.jaffree.net;

import com.github.kokorin.jaffree.ffmpeg.Input;
import com.github.kokorin.jaffree.ffmpeg.Output;
import com.github.kokorin.jaffree.process.FFHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract TCP Server implementing {@link FFHelper}.
 * <p>
 * This class is intended to be used in different ffmpeg {@link Input} & {@link Output}
 * implementations which interact with ffmpeg via TCP sockets.
 */
public abstract class TcpServer implements FFHelper {
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
    protected TcpServer(ServerSocket serverSocket) {
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
            throw new RuntimeException("TCP negotiation failed", e);
        }
    }

    protected abstract void serve(Socket socket) throws IOException;

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    public String getAddressAndPort() {
        return addressAndPort;
    }

    @Override
    public String toString() {
        return "TcpServer{addressAndPort=" + addressAndPort + '}';
    }

    protected static ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

}
