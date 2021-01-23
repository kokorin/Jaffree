package com.github.kokorin.jaffree.net;

import com.github.kokorin.jaffree.util.IOUtil;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@ThreadSafe
public class PipeOutputNegotiator implements TcpNegotiator {
    private final OutputStream destination;

    @GuardedBy("this")
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeOutputNegotiator.class);


    public PipeOutputNegotiator(OutputStream destination) {
        this.destination = destination;
    }

    public synchronized void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public synchronized void negotiate(Socket socket) throws IOException {
        try (InputStream source = socket.getInputStream()) {
            IOUtil.copy(source, destination, bufferSize);
        }
    }
}
