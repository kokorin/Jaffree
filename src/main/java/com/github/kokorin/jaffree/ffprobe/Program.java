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

import java.util.List;

/**
 * Program description.
 */
public class Program implements TagAware {
    private final ProbeData probeData;

    /**
     * Creates {@link Program} description based on provided ffprobe data.
     *
     * @param probeData ffprobe data
     */
    public Program(final ProbeData probeData) {
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
     * Returns program streams.
     *
     * @return streams
     */
    public List<Stream> getStreams() {
        return probeData.getSubDataList("streams", Stream::new);
    }

    /**
     * Returns program ID.
     *
     * @return program id
     */
    public Integer getProgramId() {
        return probeData.getInteger("program_id");
    }

    /**
     * Returns program number.
     *
     * @return program number
     */
    public Integer getProgramNum() {
        return probeData.getInteger("program_num");
    }

    /**
     * Returns number of streams in program.
     *
     * @return number of streams
     */
    public Integer getNbStreams() {
        return probeData.getInteger("nb_streams");
    }

    /**
     * Returns program start time in seconds.
     *
     * @return start time
     */
    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    /**
     * Returns program start PTS.
     * <p>
     * Timebase is hardcoded in ffmpeg code and is equal to 1_000_000.
     *
     * @return start pts
     */
    public Long getStartPts() {
        return probeData.getLong("start_pts");
    }

    /**
     * Returns program end time in seconds.
     *
     * @return end time
     */
    public Float getEndTime() {
        return probeData.getFloat("end_time");
    }

    /**
     * Returns program end PTS.
     * <p>
     * Timebase is hardcoded in ffmpeg code and is equal to 1_000_000.
     *
     * @return end pts
     */
    public Long getEndPts() {
        return probeData.getLong("end_pts");
    }

    /**
     * Returns PMT PID (packet identifier that contains program map specific data).
     *
     * @return pmt pid
     */
    public Integer getPmtPid() {
        return probeData.getInteger("pmt_pid");
    }

    /**
     * Returns PCR PID (packet identifier that contains the program clock reference).
     *
     * @return pcr pid
     */
    public Integer getPcrPid() {
        return probeData.getInteger("pcr_pid");
    }
}
