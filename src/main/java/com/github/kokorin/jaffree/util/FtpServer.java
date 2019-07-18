/*
 *    Copyright  2019 Denis Kokorin
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;


/**
 * Simple FTP server intended to work <b>only</b> with ffmpeg.
 * <p>
 * This class uses knowledge of how ffmpeg operates with FTP input:
 */
public class FtpServer implements Runnable {
    private final SeekableByteChannel channel;
    private final ServerSocket serverSocket;

    private static final byte[] NEW_LINE = "\r\n".getBytes();
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServer.class);

    public FtpServer(SeekableByteChannel channel, ServerSocket serverSocket) {
        this.channel = channel;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        LOGGER.debug("Starting FTP server {}", serverSocket);

        try (AutoCloseable toClose = serverSocket;
             ServerSocket dataServerSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {

            Socket controlSocket = serverSocket.accept();
            LOGGER.debug("Control connection established: {}", controlSocket);

            BufferedReader controlReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
            OutputStream controlOutput = controlSocket.getOutputStream();

            doGreet(controlOutput);

            boolean quit = false;
            while (!quit) {
                String line = controlReader.readLine();
                if (line == null) {
                    LOGGER.debug("Closing control connection");
                    break;
                }

                String[] commandAndArgs = line.split(" ", 2);
                String command = commandAndArgs[0].toUpperCase();
                String args = null;
                if (commandAndArgs.length == 2) {
                    args = commandAndArgs[1];
                }

                LOGGER.debug("Received command: {}", line);

                switch (command) {
                    case "USER":
                        doUser(controlOutput, args);
                        break;
                    case "TYPE":
                        doType(controlOutput, args);
                        break;
                    case "PWD":
                        doPwd(controlOutput);
                        break;
                    case "REST":
                        doRest(controlOutput, args);
                        break;
                    case "SIZE":
                        doSize(controlOutput, args);
                        break;
                    case "PASV":
                        doPasv(controlOutput, dataServerSocket);
                        break;
                    case "RETR":
                        doRetr(controlOutput, dataServerSocket);
                        break;
                    case "ABOR":
                        doAbor(controlOutput);
                        break;
                    case "QUIT":
                        quit = true;
                        break;
                    default:
                        LOGGER.warn("Command {} not supported", command);
                        doNotImplemented(controlOutput);
                        break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to serve FTP", e);
        }
    }

    protected void doGreet(OutputStream output) throws IOException {
        println(output, "220 Service ready for new user.");
    }

    protected void doUser(OutputStream output, String args) throws IOException {
        println(output, "230 User logged in, proceed.");
    }

    private void doType(OutputStream output, String args) throws IOException {
        if (!"I".equals(args)) {
            println(output, "504 Command not implemented for that parameter.");
            return;
        }

        println(output, "200 OK");
    }

    private void doPwd(OutputStream output) throws IOException {
        println(output, "257 \"\"");
    }

    private void doRest(OutputStream output, String args) throws IOException {
        Long position = null;

        try {
            position = Long.parseLong(args);
        } catch (NumberFormatException e) {
            // ignored
        }

        if (position == null) {
            println(output, "450 Requested file action not taken.");
            return;
        }

        channel.position(position);
        println(output, "350 Requested file action pending further information.");
    }

    private void doSize(OutputStream output, String args) throws IOException {
        long size = channel.size();
        println(output, "213 " + size);
    }

    private void doPasv(OutputStream output, ServerSocket dataServerSocket) throws IOException {
        int port = dataServerSocket.getLocalPort();
        int portHi = port >> 8;
        int portLow = port & 0xFF;
        println(output, "227 Entering Passive Mode (127,0,0,1," + portHi + "," + portLow + ").");
    }

    private void doRetr(OutputStream output, ServerSocket dataServerSocket) throws IOException {
        println(output, "150 File status okay; about to open data connection.");

        long copied = 0;
        try (Socket dataSocket = dataServerSocket.accept()) {
            LOGGER.debug("Data connection established: {}", dataSocket);

            copied = IOUtil.copy(Channels.newInputStream(channel), dataSocket.getOutputStream(), 1_000_000);
        } catch (SocketException e) {
            if (e.getMessage().startsWith("Connection reset by peer")) {
                LOGGER.debug("Client closed socket: {}", e.getMessage());
            } else {
                throw e;
            }

        } finally {
            LOGGER.debug("Copied {} bytes to data socket", copied);
        }
    }

    private void doAbor(OutputStream output) throws IOException {
        println(output, "226 Closing data connection.");
    }

    protected void doNotImplemented(OutputStream output) throws IOException {
        println(output, "502 Command not implemented.");
    }

    protected void println(OutputStream output, String line) throws IOException {
        output.write(line.getBytes());
        output.write(NEW_LINE);
    }
}
