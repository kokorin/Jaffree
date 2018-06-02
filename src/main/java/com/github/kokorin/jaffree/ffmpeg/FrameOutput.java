/*
 *    Copyright  2017 Denis Kokorin
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

import com.github.kokorin.jaffree.StreamType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class FrameOutput extends BaseOutput<FrameOutput> implements Output {
    private final FrameConsumer consumer;
    private final boolean alpha;
    private final ServerSocket serverSocket;

    public FrameOutput(FrameConsumer consumer, boolean alpha) {
        this.consumer = consumer;
        this.alpha = alpha;
        this.serverSocket = allocateSocket();

        setOutput("tcp://127.0.0.1:" + serverSocket.getLocalPort());
        setFormat("nut");

        // default arguments
        setCodec(StreamType.VIDEO, "rawvideo");
        String pixelFormat = alpha ? "abgr" : "bgr24";
        setPixelFormat(pixelFormat);

        setCodec(StreamType.AUDIO, "pcm_s32be");
    }
    public FrameConsumer getConsumer() {
        return consumer;
    }

    Runnable createReader() {
        return new NutFrameReader(consumer, alpha, serverSocket);
    }

    private static ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

    public static FrameOutput withConsumer(FrameConsumer consumer) {
        return new FrameOutput(consumer, false);
    }

    public static FrameOutput withConsumerAlpha(FrameConsumer consumer) {
        return new FrameOutput(consumer, true);
    }
}
