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

import java.awt.image.BufferedImage;

public class Frame {
    private int streamId;
    private long pts;
    private BufferedImage image;
    private int[] samples;

    public int getStreamId() {
        return streamId;
    }

    public Frame setStreamId(int streamId) {
        this.streamId = streamId;
        return this;
    }

    /**
     * PTS in corresponding {@link Stream} timebase.
     * E.g. Track's timebase is 44100 (audio track), timecode is 4410 - it means 0.1 second (100 milliseconds)
     *
     * @return timecode
     */
    public long getPts() {
        return pts;
    }

    public Frame setPts(long pts) {
        this.pts = pts;
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Frame setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public int[] getSamples() {
        return samples;
    }

    public Frame setSamples(int[] samples) {
        this.samples = samples;
        return this;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "streamId=" + streamId +
                ", pts=" + pts +
                '}';
    }
}
