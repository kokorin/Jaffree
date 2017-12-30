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
import com.github.kokorin.jaffree.process.LoggingStdReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameInput implements Input {

    private final List<Option> additionalOptions = new ArrayList<>();

    private FrameProducer producer;

    public FrameInput setProducer(FrameProducer producer) {
        this.producer = producer;
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

    @Override
    public void beforeExecute(FFmpeg ffmpeg) {
        ffmpeg.setStdInWriter(new NutFrameWriter(producer));
        ffmpeg.setStdOutReader(new LoggingStdReader<FFmpegResult>());
    }

    @Override
    public List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        result.addAll(additionalOptions);
        result.addAll(Arrays.asList(
                new Option("-f", "nut"),
                //new Option("-vcodec", "rawvideo"),
                //new Option("-pix_fmt", "wwer"),
                //new Option("-acodec", "pcm_s32be"),
                new Option("-i", "-")
        ));

        return result;
    }

    public static FrameInput withProducer(FrameProducer producer) {
        return new FrameInput().setProducer(producer);
    }
}
