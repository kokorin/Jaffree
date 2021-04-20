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
public class Format implements TagAware {
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
     * {@inheritDoc}
     */
    @Override
    public ProbeData getProbeData() {
        return probeData;
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
    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    /**
     * @return media duration in seconds
     */
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
