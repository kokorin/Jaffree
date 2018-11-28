
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.DSection;

import java.util.Collections;
import java.util.List;

public class Packet {

    private final DSection section;

    public Packet(DSection section) {
        this.section = section;
    }

    public List<Tag> getTag() {
        return Collections.emptyList();
    }

    public List<PacketSideData> getSideDataList() {
        return Collections.emptyList();
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