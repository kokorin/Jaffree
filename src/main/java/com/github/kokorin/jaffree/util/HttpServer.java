/*
 *    Copyright  2019 Apache commons-io participants, Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpServer implements Runnable {
    private final SeekableByteChannel channel;
    private final ServerSocket serverSocket;
    private final AtomicInteger count = new AtomicInteger();
    private final Object lock = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer(SeekableByteChannel channel, ServerSocket serverSocket) {
        this.channel = channel;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (AutoCloseable toClose = serverSocket) {
            do {
                final Socket socket = serverSocket.accept();
                count.incrementAndGet();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serve(socket);
                        } catch (IOException e) {
                            LOGGER.warn("Failed to serve request", e);
                        } finally {
                            count.decrementAndGet();
                        }
                    }
                }).start();

                LOGGER.debug("Connection accepted");
            } while (true);
        } catch (Exception e) {
            LOGGER.warn("Exception while serving request", e);
        }
    }

    protected void serve(Socket socket) throws IOException {
        boolean keepAlive = true;
        try (Closeable toClose = socket) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (keepAlive) {

                String verbAndVersion = reader.readLine();
                Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }

                    String[] nameAndValue = line.split(": ");
                    String name = nameAndValue[0];
                    String value = nameAndValue[1];
                    headers.put(name, value);
                }

                if (verbAndVersion.startsWith("GET ")) {
                    keepAlive = doGet(headers, socket.getOutputStream());
                } else {
                    throw new RuntimeException("Unsupported verb: " + verbAndVersion);
                }
            }
        }
    }

    protected boolean doGet(Map<String, String> headers, OutputStream output) throws IOException {
        LOGGER.debug("Serving GET request");

        String range = headers.get("Range");
        if (range != null && !range.startsWith("bytes=")) {
            throw new RuntimeException("Unknown Range unit: " + range);
        }

        Long firstByte = null;
        Long lastByte = null;

        if (range != null) {
            String[] startAndEnd = range.substring(6).split("-");
            String firstStr = startAndEnd[0];
            if (!firstStr.isEmpty()) {
                firstByte = Long.valueOf(firstStr);
            }
            if (startAndEnd.length > 1) {
                String lastStr = startAndEnd[1];
                if (!lastStr.isEmpty()) {
                    lastByte = Long.valueOf(lastStr);
                }
            }
        }

        boolean keepAlive = "keep-alive".equalsIgnoreCase(headers.get("Connection"));
        String connectionHeader = "Connection: " + (keepAlive ? "keep-alive" : "close") + "\r\n";

        if (firstByte == null && lastByte == null) {
            output.write("HTTP/1.1 200 OK\r\n".getBytes());
            output.write(connectionHeader.getBytes());
            output.write(("Content-Length: " + channel.size() + "\r\n").getBytes());
            output.write("\r\n".getBytes());

            synchronized (lock) {
                channel.position(0);
                IOUtil.copy(Channels.newInputStream(channel), output);
            }
        } else {
            if (firstByte != null && lastByte == null) {
                lastByte = channel.size() - 1;
            } else if (firstByte == null) {
                long length = lastByte;
                lastByte = channel.size();
                firstByte = channel.size() - length;
            }

            LOGGER.debug("Received range: {}, will serve from {} to {}", range, firstByte, lastByte);

            long length = lastByte - firstByte + 1;
            output.write("HTTP/1.1 206 Partial Content\r\n".getBytes());
            output.write(connectionHeader.getBytes());
            output.write("Accept-Ranges: bytes\r\n".getBytes());
            output.write("Content-Type: application/octet-stream\r\n".getBytes());
            output.write(("Content-Length: " + length + "\r\n").getBytes());
            output.write(("Content-Range: bytes " + firstByte + "-" + lastByte + "/" + channel.size() + "\r\n").getBytes());
            output.write("\r\n".getBytes());

            synchronized (lock) {
                channel.position(firstByte);
                IOUtil.copy(Channels.newInputStream(channel), output, 1_000_000, length);
            }
        }

        return keepAlive;
    }
}
