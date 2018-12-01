/*
 *    Copyright  2018 Denis Kokorin
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
import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.DTag;

import java.util.Collections;
import java.util.List;

public class Packet {

    private final DSection section;

    public Packet(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public List<PacketSideData> getSideDataList() {
        return section.getSections("SIDE_DATA", new DSection.SectionConverter<PacketSideData>() {
            @Override
            public PacketSideData convert(DSection dSection) {
                return new PacketSideData(dSection);
            }
        });
    }
    public StreamType getCodecType() {
        return section.getStreamType("codec_type");
    }

    public int getStreamIndex() {
        return section.getInteger("stream_index");
    }

    public Long getPts() {
        return section.getLong("pts");
    }

    public Float getPtsTime() {
        return section.getFloat("pts_time");
    }

    public Long getDts() {
        return section.getLong("dts");
    }

    public Float getDtsTime() {
        return section.getFloat("dts_time");
    }

    public Long getDuration() {
        return section.getLong("duration");
    }

    public Float getDurationTime() {
        return section.getFloat("duration_time");
    }

    public Long getConvergenceDuration() {
        return section.getLong("convergence_duration");
    }

    public Float getConvergenceDurationTime() {
        return section.getFloat("convergence_duration_time");
    }

    public long getSize() {
        return section.getLong("size");
    }

    public Long getPos() {
        return section.getLong("pos");
    }

    public String getFlags() {
        return section.getString("flags");
    }

    public String getData() {
        return section.getString("data");
    }

    public String getDataHash() {
        return section.getString("data_hash");
    }
}