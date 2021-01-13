package com.github.kokorin.jaffree.network;

import java.io.IOException;
import java.net.Socket;

public class NegotiatingTcpServer extends TcpServer {
    protected final TcpNegotiator negotiator;

    public NegotiatingTcpServer(TcpNegotiator negotiator) {
        this.negotiator = negotiator;
    }

    @Override
    protected void serve(Socket socket) throws IOException {
        negotiator.negotiate(socket);
    }

}
