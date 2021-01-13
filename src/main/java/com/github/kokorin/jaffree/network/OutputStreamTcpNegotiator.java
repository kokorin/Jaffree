package com.github.kokorin.jaffree.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OutputStreamTcpNegotiator implements TcpNegotiator {
    private final OutputStreamSupplier supplier;

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputStreamTcpNegotiator.class);

    public OutputStreamTcpNegotiator(OutputStreamSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public void negotiate(Socket socket) throws IOException {
        try (OutputStream outputStream = socket.getOutputStream()) {
            LOGGER.debug("Passing output stream to supplier: {}", supplier);
            supplier.supply(outputStream);
        }
    }
}
