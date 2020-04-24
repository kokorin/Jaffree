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

/**
 * Represents video/audio data to be encoded or has been decoded.
 *
 * <b>Note</b>: image and samples must not be changed after creating Frame instance.
 * Otherwise it may affect (or event corrupt) produced media, because Jaffree internally
 * maintains frame reordering buffer while producing video.
 */
public class Frame {
    private final int streamId;
    private final long pts;
    private final BufferedImage image;
    private final int[] samples;

    /**
     * Constructor which creates Video Frame, samples are set to null
     *
     * @param streamId streamId
     * @param pts      pts
     * @param image    image
     */
    public Frame(int streamId, long pts, BufferedImage image) {
        this(streamId, pts, image, null);
    }

    /**
     * Constructor which creates Audio Frame, image is set to null
     *
     * @param streamId streamId
     * @param pts      pts
     * @param samples  samples
     */
    public Frame(int streamId, long pts, int[] samples) {
        this(streamId, pts, null, samples);
    }

    public Frame(int streamId, long pts, BufferedImage image, int[] samples) {
        if (image != null && samples != null) {
            throw new IllegalArgumentException("Only one of image and samples parameters may be non null");
        }
        if (image == null && samples == null) {
            throw new IllegalArgumentException("One of image and samples parameters must be non null");
        }

        this.streamId = streamId;
        this.pts = pts;
        this.image = image;
        this.samples = samples;
    }

    public int getStreamId() {
        return streamId;
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

    public BufferedImage getImage() {
        return image;
    }

    public int[] getSamples() {
        return samples;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "streamId=" + streamId +
                ", pts=" + pts +
                ", image?=" + (image != null) +
                ", samples?=" + (samples != null) +
                '}';
    }
}
