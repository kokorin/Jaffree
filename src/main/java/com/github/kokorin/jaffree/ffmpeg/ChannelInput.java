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

import com.github.kokorin.jaffree.util.FtpServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SeekableByteChannel;

public class ChannelInput extends SocketInput<ChannelInput> implements Input {
    private final SeekableByteChannel channel;

    public ChannelInput(String fileName, SeekableByteChannel channel) {
        super("ftp", "/" + fileName);
        this.channel = channel;
    }

    @Override
    Negotiator negotiator() {
        return new Negotiator() {
            @Override
            public void negotiateAndClose(ServerSocket serverSocket) throws IOException {
                try (Closeable toClose = serverSocket) {
                    Runnable server = new FtpServer(channel, serverSocket);
                    server.run();
                }
            }
        };
    }

    public static ChannelInput fromChannel(String fileName, SeekableByteChannel channel) {
        return new ChannelInput(fileName, channel);
    }
}
