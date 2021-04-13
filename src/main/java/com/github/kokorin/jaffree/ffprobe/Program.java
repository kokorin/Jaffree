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
import com.github.kokorin.jaffree.ffprobe.data.ProbeDataConverter;

import java.util.List;

// TODO check what timebase are used for StartPts & EndPts
public class Program implements TagAware {
    private final ProbeData probeData;

    public Program(ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProbeData getProbeData() {
        return probeData;
    }

    public List<Stream> getStreams() {
        return probeData.getSubDataList("streams", new ProbeDataConverter<Stream>() {
            @Override
            public Stream convert(ProbeData probeData) {
                return new Stream(probeData);
            }
        });
    }

    public Integer getProgramId() {
        return probeData.getInteger("program_id");
    }

    public Integer getProgramNum() {
        return probeData.getInteger("program_num");
    }

    public Integer getNbStreams() {
        return probeData.getInteger("nb_streams");
    }

    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    public Long getStartPts() {
        return probeData.getLong("start_pts");
    }

    public Float getEndTime() {
        return probeData.getFloat("end_time");
    }

    public Long getEndPts() {
        return probeData.getLong("end_pts");
    }

    public Integer getPmtPid() {
        return probeData.getInteger("pmt_pid");
    }

    public Integer getPcrPid() {
        return probeData.getInteger("pcr_pid");
    }
}
