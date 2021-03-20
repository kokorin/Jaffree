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
import com.github.kokorin.jaffree.ffprobe.data.ProbeDataConverter;

import java.util.List;

public class Packet implements PacketFrameSubtitle {

    private final ProbeData probeData;

    public Packet(ProbeData probeData) {
        this.probeData = probeData;
    }

    public ProbeData getProbeData() {
        return probeData;
    }

    public Long getPts() {
        return probeData.getLong("pts");
    }

    public Float getPtsTime() {
        return probeData.getFloat("pts_time");
    }

    // TODO Does Packet contain any tags?
    public String getTag(String name) {
        return probeData.getSubData("tags").getString(name);
    }

    public List<PacketSideData> getSideDataList() {
        return probeData.getSubDataList("SIDE_DATA", new ProbeDataConverter<PacketSideData>() {
            @Override
            public PacketSideData convert(ProbeData probeData) {
                return new PacketSideData(probeData);
            }
        });
    }

    public StreamType getCodecType() {
        return probeData.getStreamType("codec_type");
    }

    public Integer getStreamIndex() {
        return probeData.getInteger("stream_index");
    }

    public Long getDts() {
        return probeData.getLong("dts");
    }

    public Float getDtsTime() {
        return probeData.getFloat("dts_time");
    }

    public Long getDuration() {
        return probeData.getLong("duration");
    }

    public Float getDurationTime() {
        return probeData.getFloat("duration_time");
    }

    public Long getConvergenceDuration() {
        return probeData.getLong("convergence_duration");
    }

    public Float getConvergenceDurationTime() {
        return probeData.getFloat("convergence_duration_time");
    }

    public Long getSize() {
        return probeData.getLong("size");
    }

    public Long getPos() {
        return probeData.getLong("pos");
    }

    public String getFlags() {
        return probeData.getString("flags");
    }

    public String getData() {
        return probeData.getString("data");
    }

    public String getDataHash() {
        return probeData.getString("data_hash");
    }
}