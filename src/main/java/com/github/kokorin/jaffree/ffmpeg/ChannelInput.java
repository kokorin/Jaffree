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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.net.FtpServer;

import java.nio.channels.SeekableByteChannel;

/**
 * {@link ChannelInput} is the implementation of {@link Input}
 * which allows usage of {@link SeekableByteChannel} as ffmpeg input.
 */
public class ChannelInput extends TcpInput<ChannelInput> implements Input {

    /**
     * Creates {@link ChannelInput}.
     * <p>
     * ffmpeg uses fileName's extension to autodetect input format
     *
     * @param fileName file name
     * @param channel  byte channel
     */
    public ChannelInput(final String fileName, final SeekableByteChannel channel) {
        super("ftp", "/" + fileName, FtpServer.onRandomPorts(channel));
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
