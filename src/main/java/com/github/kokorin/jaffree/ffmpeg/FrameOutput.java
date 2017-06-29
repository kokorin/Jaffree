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
    private boolean video;
    private boolean audio;

    private final FrameConsumer consumer;

    public FrameOutput(FrameConsumer consumer) {
        this.consumer = consumer;
    }

    public FrameOutput extractVideo(boolean video) {
        this.video = video;
        return this;
    }

    public FrameOutput extractAudio(boolean audio) {
        this.audio = audio;
        return this;
    }

    public FrameConsumer getConsumer() {
        return consumer;
    }

    @Override
    public void beforeExecute(FFmpeg fFmpeg) {

    }

    @Override
    public List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        result.add(new Option("-f", "matroska"));

        if (video) {
            result.add(new Option("-vcodec", "rawvideo"));
            result.add(new Option("-pix_fmt", "yuv420p"));
        } else {
            result.add(new Option("-vn"));
        }

        if (audio) {
            result.add(new Option("-acodec", "pcm_s32le"));
        } else {
            result.add(new Option("-an"));
        }

        result.add(new Option("-"));

        return result;
    }

    public static FrameOutput withConsumer(FrameConsumer consumer) {
        return new FrameOutput(consumer);
    }
}
