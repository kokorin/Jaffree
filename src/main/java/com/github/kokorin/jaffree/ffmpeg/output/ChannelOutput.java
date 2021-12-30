/*
 *    Copyright 2019-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg.output;

import com.github.kokorin.jaffree.ffmpeg.input.ChannelInput;
import com.github.kokorin.jaffree.net.FtpServer;

import java.nio.channels.SeekableByteChannel;

/**
 * {@link Output} implementationwhich allows usage of {@link SeekableByteChannel} as ffmpeg output.
 */
public class ChannelOutput extends TcpOutput<ChannelOutput> implements Output {

    /**
     * Creates {@link ChannelOutput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect output format
     *
     * @param fileName file name
     * @param channel  byte channel
     */
    public ChannelOutput(final String fileName, final SeekableByteChannel channel) {
        super("ftp", "/" + fileName, FtpServer.onRandomPorts(channel));
        this.addArguments("-ftp-write-seekable", "1");
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
