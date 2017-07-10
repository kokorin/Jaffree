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

import java.util.Arrays;
import java.util.List;

public class FrameInput implements Input {

    private FrameProducer producer;

    public FrameInput setProducer(FrameProducer producer) {
        this.producer = producer;
        return this;
    }

    @Override
    public void beforeExecute(FFmpeg ffmpeg) {
        ffmpeg.setStdInWriter(new FrameWriter(producer));
        ffmpeg.setStdErrReader(new LoggingStdReader<FFmpegResult>());
        ffmpeg.setStdOutReader(new LoggingStdReader<FFmpegResult>());
    }

    @Override
    public List<Option> buildOptions() {
        return Arrays.asList(
                new Option("-f", "matroska"),
                new Option("-vcodec", "rawvideo"),
                //new Option("-pix_fmt", "yuv420p"),
                new Option("-i", "-")
        );
    }

    public static FrameInput withProducer(FrameProducer producer) {
        return new FrameInput().setProducer(producer);
    }
}
