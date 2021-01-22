/*
 *    Copyright  2019-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.net.FtpServer;

import java.nio.channels.SeekableByteChannel;

/**
 * {@link ChannelInput} is the implementation of {@link Input}
 * which allows usage of {@link SeekableByteChannel} as ffprobe input.
 */
public class ChannelInput extends TcpInput {

    /**
     * Creates {@link ChannelInput}.
     *
     * @param channel byte channel
     */
    public ChannelInput(final SeekableByteChannel channel) {
        this("", channel);
    }

    /**
     * Creates {@link ChannelInput}.
     *
     * @param channel  byte channel
     * @param fileName file name
     */
    public ChannelInput(final String fileName, final SeekableByteChannel channel) {
        super("ftp", "/" + fileName, FtpServer.onRandomPorts(channel));
    }

    /**
     * Creates {@link ChannelInput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect input format
     *
     * @param channel byte channel
     * @return ChannelInput
     */
    public static ChannelInput fromChannel(final SeekableByteChannel channel) {
        return new ChannelInput(channel);
    }

    /**
     * Creates {@link ChannelInput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect input format
     *
     * @param fileName file name
     * @param channel  byte channel
     * @return ChannelInput
     */
    public static ChannelInput fromChannel(final String fileName,
                                           final SeekableByteChannel channel) {
        return new ChannelInput(fileName, channel);
    }
}
