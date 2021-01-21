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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.net.FtpServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SeekableByteChannel;

/**
 * {@link ChannelOutput} is the implementation of {@link Output}
 * which allows usage of {@link SeekableByteChannel} as ffmpeg output.
 */
public class ChannelOutput extends SocketOutput<ChannelOutput> implements Output {
    private final SeekableByteChannel channel;

    /**
     * Creates {@link ChannelOutput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect output format
     *
     * @param filename file name
     * @param channel  byte channel
     */
    public ChannelOutput(final String filename, final SeekableByteChannel channel) {
        super("ftp", "/" + filename);
        this.channel = channel;
        this.addArguments("-ftp-write-seekable", "1");
    }

    /**
     * Creates {@link Negotiator} which adapts byte chanel to be used as ffmpeg output.
     *
     * @return negotiator
     */
    @Override
    Negotiator negotiator() {
        return new Negotiator() {
            @Override
            public void negotiateAndClose(final ServerSocket serverSocket) throws IOException {
                try (Closeable toClose = serverSocket) {
                    Runnable server = new FtpServer(channel, serverSocket);
                    server.run();
                }
            }
        };
    }

    /**
     * Creates {@link ChannelInput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect output format
     *
     * @param filename file name
     * @param channel  byte channel
     * @return ChannelOutput
     */
    public static ChannelOutput toChannel(final String filename,
                                          final SeekableByteChannel channel) {
        return new ChannelOutput(filename, channel);
    }
}
