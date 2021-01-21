package com.github.kokorin.jaffree.net;

import java.io.IOException;
import java.net.Socket;

public interface TcpNegotiator {
    void negotiate(Socket socket) throws IOException;
}

