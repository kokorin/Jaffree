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

import java.util.ArrayList;
import java.util.List;

public class FrameOutput implements Output {
    private boolean video = true;
    private boolean alpha = false;
    private boolean audio = true;
    private final List<Option> additionalOptions = new ArrayList<>();
    private int port;

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

    public FrameOutput addOption(String key) {
        additionalOptions.add(new Option(key));
        return this;
    }

    public FrameOutput addOption(String key, String value) {
        additionalOptions.add(new Option(key, value));
        return this;
    }

    public FrameConsumer getConsumer() {
        return consumer;
    }

    void setPort(int port) {
        this.port = port;
    }

    Runnable createReader() {
        checkPort();

        return new NutFrameReader(consumer, alpha, port);
    }

    @Override
    public List<Option> buildOptions() {
        checkPort();

        List<Option> result = new ArrayList<>();

        result.add(new Option("-f", "nut"));

        if (video) {
            result.add(new Option("-vcodec", "rawvideo"));
            String pixelFormat = alpha ? "abgr" : "bgr24";
            result.add(new Option("-pix_fmt", pixelFormat));
        } else {
            result.add(new Option("-vn"));
        }

        if (audio) {
            result.add(new Option("-acodec", "pcm_s32be"));
        } else {
            result.add(new Option("-an"));
        }

        result.addAll(additionalOptions);

        //result.add(new Option("-"));
        result.add(new Option("tcp://127.0.0.1:" + port + "?listen=1"));

        return result;
    }

    private void checkPort() {
        if (port == 0) {
            throw new RuntimeException("TCP Port must be set!");
        }
    }

    public static FrameOutput withConsumer(FrameConsumer consumer) {
        return new FrameOutput(consumer);
    }
}
