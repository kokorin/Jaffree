package com.github.kokorin.jaffree.network;

import java.io.IOException;
import java.net.Socket;

public interface TcpNegotiator {
    void negotiate(Socket socket) throws IOException;
}

