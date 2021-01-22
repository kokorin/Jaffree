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
import java.net.SocketException;

@ThreadSafe
public class PipeInputNegotiator implements TcpNegotiator {
    private final InputStream source;

    @GuardedBy("this")
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeInputNegotiator.class);

    public PipeInputNegotiator(InputStream source) {
        this.source = source;
    }

    public synchronized void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public synchronized void negotiate(Socket socket) throws IOException {
        try (OutputStream destination = socket.getOutputStream()) {
            IOUtil.copy(source, destination, bufferSize);
        } catch (SocketException e) {
            // Client (ffmpeg) has no way to notify server that no more data is needed.
            // It just closes TCP connection on its side.
            LOGGER.debug("Ignoring exception: " + e.getMessage());
        }
    }
}
