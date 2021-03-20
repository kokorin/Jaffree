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

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * Format description.
 */
// TODO test properties
public class Format {
    private final ProbeData probeData;

    /**
     * Creates {@link Format} description based on provided data sections.
     *
     * @param probeData data section
     */
    public Format(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for current {@link Format}.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * Return tag value by name
     * @param name tag name
     * @return tag value
     */
    // TODO Does Format contain any tags?
    // TODO Type-specific getters: Integer, Long, etc
    public String getTag(String name) {
        return probeData.getSubData("tags").getString(name);
    }

    /**
     * @return file name
     */
    public String getFilename() {
        return probeData.getString("filename");
    }

    /**
     * @return number of streams
     */
    public Integer getNbStreams() {
        return probeData.getInteger("nb_streams");
    }

    /**
     * @return number of programs
     */
    public Integer getNbPrograms() {
        return probeData.getInteger("nb_programs");
    }

    /**
     * @return format name
     */
    public String getFormatName() {
        return probeData.getString("format_name");
    }

    /**
     * @return format long name
     */
    public String getFormatLongName() {
        return probeData.getString("format_long_name");
    }

    /**
     * @return media start time in seconds
     */
    // TODO: getter with TimeUnit?
    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    /**
     * @return media duration in seconds
     */
    // TODO: getter with TimeUnit?
    public Float getDuration() {
        return probeData.getFloat("duration");
    }

    /**
     * @return media size in bytes
     */
    public Long getSize() {
        return probeData.getLong("size");
    }

    /**
     * @return media bitrate in bits per second
     */
    public Long getBitRate() {
        return probeData.getLong("bit_rate");
    }

    /**
     * Returns detected format score.
     * <p>
     * FFprobe assigns corresponding probe score to each known format during input analysis.
     * The return format by ffprobe is the one with highest score.
     * <p>
     * Higher values (up to 100) mean higher probability of correct format detection.
     *
     * @return format probe score
     */
    public Integer getProbeScore() {
        return probeData.getInteger("probe_score");
    }
}
