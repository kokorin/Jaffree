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

import com.github.kokorin.jaffree.Option;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameInput implements Input {

    private final List<Option> additionalOptions = new ArrayList<>();

    private FrameProducer producer;

    private boolean alpha;
    private ServerSocket serverSocket;

    public FrameInput setProducer(FrameProducer producer) {
        this.producer = producer;
        return this;
    }

    /**
     * Whether produced video stream should contain alpha channel
     * @param alpha alpha
     * @return this
     */
    public FrameInput produceAlpha(boolean alpha) {
        this.alpha = alpha;
        return this;
    }

    public FrameInput addOption(Option option) {
        additionalOptions.add(option);
        return this;
    }

    public FrameInput addOption(String key, String value) {
        additionalOptions.add(new Option(key, value));
        return this;
    }

    Runnable createWriter() {
        allocateSocket();
        return new NutFrameWriter(producer, alpha, serverSocket);
    }

    @Override
    public List<Option> buildOptions() {
        allocateSocket();

        List<Option> result = new ArrayList<>();

        result.addAll(additionalOptions);
        result.addAll(Arrays.asList(
                new Option("-f", "nut"),
                new Option("-i", "tcp://127.0.0.1:" + serverSocket.getLocalPort())
        ));

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

    public static FrameInput withProducer(FrameProducer producer) {
        return new FrameInput().setProducer(producer);
    }
}
