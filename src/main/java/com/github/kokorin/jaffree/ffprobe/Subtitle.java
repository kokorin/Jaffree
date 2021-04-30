/*
 *    Copyright 2018-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * Subtitle description.
 */
public class Subtitle implements FrameSubtitle, PacketFrameSubtitle {
    private final ProbeData probeData;

    /**
     * Creates {@link Subtitle} description based on provided ffprobe data.
     *
     * @param probeData ffprobe data
     */
    public Subtitle(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters.
     *
     * @return probe data
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * Returns presentation timestamp.
     * <p>
     * Timebase is hardcoded in ffmpeg code and is equal to 1_000_000.
     *
     * @return pts
     */
    public Long getPts() {
        return probeData.getLong("pts");
    }

    /**
     * Returns presentation time in seconds.
     *
     * @return pts
     */
    public Float getPtsTime() {
        return probeData.getFloat("pts_time");
    }

    /**
     * Always returns {@link StreamType#SUBTITLE}.
     *
     * @return subtitle StreamType
     */
    public StreamType getMediaType() {
        return probeData.getStreamType("media_type");
    }

    /**
     * @return stream format
     */
    public Integer getFormat() {
        return probeData.getInteger("format");
    }

    /**
     * @return start display time
     */
    public Integer getStartDisplayTime() {
        return probeData.getInteger("start_display_time");
    }

    /**
     * @return end display time
     */
    public Integer getEndDisplayTime() {
        return probeData.getInteger("end_display_time");
    }

    /**
     * @return number of rectangles
     */
    public Integer getNumRects() {
        return probeData.getInteger("num_rects");
    }
}
