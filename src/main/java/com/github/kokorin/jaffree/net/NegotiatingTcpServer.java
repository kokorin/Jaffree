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
