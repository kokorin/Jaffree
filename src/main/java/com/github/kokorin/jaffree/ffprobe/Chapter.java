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

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * Chapter description.
 */
public class Chapter {
    private final ProbeData probeData;

    /**
     * Creates {@link Chapter} description based on provided data sections.
     *
     * @param probeData data section
     */
    public Chapter(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for the current Chapter.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    public String getTag(String name) {
        return probeData.getSubDataString("tags", name);
    }

    /**
     * Returns Chapter ID.
     *
     * @return id
     */
    public int getId() {
        return probeData.getInteger("id");
    }

    /**
     * Returns Chapter time base.
     *
     * @return time base
     */
    public Rational getTimeBase() {
        return probeData.getRational("time_base");
    }

    /**
     * Returns chapter start PTS (in time base).
     *
     * @return start PTS
     * @see #getTimeBase()
     */
    public Long getStart() {
        return probeData.getLong("start");
    }

    /**
     * Returns Chapter start time in seconds.
     *
     * @return start time
     */
    public Double getStartTime() {
        return probeData.getDouble("start_time");
    }

    /**
     * Returns chapter end PTS (in time base).
     *
     * @return end PTS
     * @see #getTimeBase()
     */
    public Long getEnd() {
        return probeData.getLong("end");
    }

    /**
     * Returns Chapter end time in seconds.
     *
     * @return end time
     */
    public Double getEndTime() {
        return probeData.getDouble("end_time");
    }
}
