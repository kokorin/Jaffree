package com.github.kokorin.jaffree.ffmpeg;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TcpOutput<T extends TcpOutput<T>> extends BaseOutput<T> implements Output {
    private final ServerSocket serverSocket;

    public TcpOutput() {
        this.serverSocket = allocateSocket();

        setOutput("tcp://127.0.0.1:" + serverSocket.getLocalPort()/* + "?timeout=1000000"*/);
    }

    @Override
    public final Runnable helperThread() {
        final Reader reader = reader();

        return new Runnable() {
            @Override
            public void run() {
                try (Closeable toClose = serverSocket;
                     Socket socket = serverSocket.accept();
                     InputStream input = socket.getInputStream()) {
                    reader.read(input);
                } catch (IOException e) {
                    throw  new RuntimeException("Failed to read from socket " + serverSocket, e);
                }
            }
        };
    }

    protected abstract Reader reader();

    protected ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

    public interface Reader {
        void read(InputStream in);
    }
}
