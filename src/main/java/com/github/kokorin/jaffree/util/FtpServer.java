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
import java.io.InputStream;
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
 * This class <b>is not intended for use as production FTP server</b>
 * since it uses knowledge of how ffmpeg operates with FTP input & output.
 */
public class FtpServer implements Runnable {
    private final SeekableByteChannel channel;
    private final ServerSocket serverSocket;
    private final int bufferSize = 1_000_000;

    private static final byte[] NEW_LINE = "\r\n".getBytes();
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServer.class);

    /**
     * Creates {@link FtpServer}.
     *
     * @param channel      channel to read/write to/from
     * @param serverSocket socket to accept connections
     */
    // TODO introduce buffer size constructor parameter
    public FtpServer(final SeekableByteChannel channel, final ServerSocket serverSocket) {
        this.channel = channel;
        this.serverSocket = serverSocket;
    }

    /**
     * Starts FTP server.
     */
    @Override
    public void run() {
        LOGGER.debug("Starting FTP server {}", serverSocket);

        InetAddress address = InetAddress.getLoopbackAddress();
        try (AutoCloseable toClose = serverSocket;
             ServerSocket dataServerSocket = new ServerSocket(0, 1, address)) {

            Socket controlSocket = serverSocket.accept();
            LOGGER.debug("Control connection established: {}", controlSocket);

            try (BufferedReader controlReader = new BufferedReader(
                    new InputStreamReader(controlSocket.getInputStream()));
                 OutputStream controlOutput = controlSocket.getOutputStream()) {

                operate(controlReader, controlOutput, dataServerSocket);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to serve FTP", e);
        }
    }

    /**
     * Operates as FTP server: accepts control commands via controlReader, sends control responses
     * via controlOutput, uses dataServerSocket for data transfer.
     *
     * @param controlReader    control input
     * @param controlOutput    control output
     * @param dataServerSocket server socket for data transfer
     * @throws IOException socket IO exception
     */
    protected void operate(final BufferedReader controlReader,
                           final OutputStream controlOutput,
                           final ServerSocket dataServerSocket) throws IOException {
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
                case "STOR":
                    doStor(controlOutput, dataServerSocket, args);
                    break;
                case "ABOR":
                    doAbor(controlOutput);
                    break;
                case "FEAT":
                case "EPSV":
                    // intentional fall through
                    doNotImplemented(controlOutput);
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
    }

    /**
     * Sends greet response after establishing control connection.
     *
     * @param output output to write response
     * @throws IOException socket IO exception
     */
    protected void doGreet(final OutputStream output) throws IOException {
        println(output, "220 Service ready for new user.");
    }

    /**
     * Sends response to USER control command.
     *
     * @param output output to write response
     * @param args   arguments, ignored
     * @throws IOException socket IO exception
     */
    protected void doUser(final OutputStream output, final String args) throws IOException {
        println(output, "230 User logged in, proceed.");
    }

    /**
     * Sends response to TYPE control command.
     *
     * @param output output to write response
     * @param args   arguments, ignored
     * @throws IOException socket IO exception
     */
    private void doType(final OutputStream output, final String args) throws IOException {
        if (!"I".equals(args)) {
            println(output, "504 Command not implemented for that parameter.");
            return;
        }

        println(output, "200 OK");
    }

    /**
     * Sends response to PWD control command.
     *
     * @param output output to write response
     * @throws IOException socket IO exception
     */
    private void doPwd(final OutputStream output) throws IOException {
        println(output, "257 \"\"");
    }

    /**
     * Sends response to REST control command.
     *
     * @param output output to write response
     * @param args arguments, ignored
     * @throws IOException socket IO exception
     */
    private void doRest(final OutputStream output, final String args) throws IOException {
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

    /**
     * Sends response to SIZE control command.
     *
     * @param output output to write response
     * @param args arguments, ignored
     * @throws IOException socket IO exception
     */
    private void doSize(final OutputStream output, final String args) throws IOException {
        long size = channel.size();
        println(output, "213 " + size);
    }

    /**
     * Sends response to PASV control command.
     *
     * @param output           output to write response
     * @param dataServerSocket server socket for data transfer
     * @throws IOException socket IO exception
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void doPasv(final OutputStream output, final ServerSocket dataServerSocket)
            throws IOException {
        int port = dataServerSocket.getLocalPort();
        int portHi = port >> 8;
        int portLow = port & 0xFF;
        println(output, "227 Entering Passive Mode (127,0,0,1," + portHi + "," + portLow + ").");
    }

    /**
     * Sends response to RETR control command, accepts data connection amd transfers data.
     *
     * @param output           output to write response
     * @param dataServerSocket server socket for data transfer
     * @throws IOException socket IO exception
     */
    private void doRetr(final OutputStream output, final ServerSocket dataServerSocket)
            throws IOException {
        println(output, "150 File status okay; about to open data connection.");

        long copied = 0;
        try (Socket dataSocket = dataServerSocket.accept();
             OutputStream dataOutput = dataSocket.getOutputStream()) {
            LOGGER.debug("Data connection established: {}", dataSocket);

            copied = IOUtil.copy(Channels.newInputStream(channel), dataOutput, bufferSize);
        } catch (SocketException e) {
            // ffmpeg can close connection without fully reading requested data.
            // This is not an error.
            // "Connection reset" is thrown on Linux (Ubuntu) & Windows
            // "Broken pipe" is thrown on MacOS
            String message = e.getMessage();
            if (message.startsWith("Connection reset") || message.startsWith("Broken pipe")) {
                LOGGER.debug("Client closed socket: {}", e.getMessage());
            } else {
                throw e;
            }
        } finally {
            LOGGER.debug("Copied {} bytes to data socket", copied);
        }
    }

    /**
     * Sends response to STOR control command.
     *
     * @param output           output to write response
     * @param dataServerSocket server socket for data transfer
     * @param path             path to store a file, ignored
     * @throws IOException socket IO exception
     */
    private void doStor(final OutputStream output, final ServerSocket dataServerSocket,
                        final String path) throws IOException {
        println(output, "150 File status okay; about to open data connection.");

        long copied = 0;
        try (Socket dataSocket = dataServerSocket.accept();
             InputStream dataInput = dataSocket.getInputStream()) {
            LOGGER.debug("Data connection established: {}", dataSocket);

            copied = IOUtil.copy(dataInput, Channels.newOutputStream(channel), bufferSize);
        } catch (SocketException e) {
            if (e.getMessage().startsWith("Connection reset by peer")) {
                LOGGER.debug("Client closed socket: {}", e.getMessage());
            } else {
                throw e;
            }

        } finally {
            LOGGER.debug("Copied {} bytes from data socket", copied);
        }
    }

    /**
     * Sends response to ABOR control command.
     *
     * @param output output to write response
     * @throws IOException socket IO exception
     */
    private void doAbor(final OutputStream output) throws IOException {
        println(output, "226 Closing data connection.");
    }

    /**
     * Sends response to non-implemented control commands.
     *
     * @param output output to write response
     * @throws IOException socket IO exception
     */
    protected void doNotImplemented(final OutputStream output) throws IOException {
        println(output, "502 Command not implemented.");
    }

    private void println(final OutputStream output, final String line) throws IOException {
        LOGGER.debug("Responding: {}", line);
        output.write(line.getBytes());
        output.write(NEW_LINE);
    }
}
