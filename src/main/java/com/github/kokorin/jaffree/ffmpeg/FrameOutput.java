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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameOutput implements Output {
    private boolean video = true;
    private boolean alpha = false;
    private boolean audio = true;
    private final List<String> additionalArguments = new ArrayList<>();
    private ServerSocket serverSocket;

    private final FrameConsumer consumer;

    public FrameOutput(FrameConsumer consumer) {
        this.consumer = consumer;
    }

    public FrameOutput extractVideo(boolean video) {
        this.video = video;
        return this;
    }

    /**
     * whether to extract video alpha channel (transparency)
     *
     * @param alpha extract alpha
     * @return this
     */
    public FrameOutput extractAlpha(boolean alpha) {
        this.alpha = alpha;
        return this;
    }

    public FrameOutput extractAudio(boolean audio) {
        this.audio = audio;
        return this;
    }

    public FrameOutput addArgument(String key) {
        additionalArguments.add(key);
        return this;
    }

    public FrameOutput addArguments(String key, String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return this;
    }

    public FrameConsumer getConsumer() {
        return consumer;
    }

    Runnable createReader() {
        allocateSocket();

        return new NutFrameReader(consumer, alpha, serverSocket);
    }

    @Override
    public List<String> buildArguments() {
        allocateSocket();

        List<String> result = new ArrayList<>();

        result.addAll(Arrays.asList("-f", "nut"));

        if (video) {
            result.addAll(Arrays.asList("-vcodec", "rawvideo"));
            String pixelFormat = alpha ? "abgr" : "bgr24";
            result.addAll(Arrays.asList("-pix_fmt", pixelFormat));
        } else {
            result.add("-vn");
        }

        if (audio) {
            result.addAll(Arrays.asList("-acodec", "pcm_s32be"));
        } else {
            result.add("-an");
        }

        result.addAll(additionalArguments);

        result.add("tcp://127.0.0.1:" + serverSocket.getLocalPort());

        return result;
    }

    private void allocateSocket() {
        if (serverSocket != null) {
            return;
        }

        try {
            serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate scoket", e);
        }
    }

    public static FrameOutput withConsumer(FrameConsumer consumer) {
        return new FrameOutput(consumer);
    }
}
