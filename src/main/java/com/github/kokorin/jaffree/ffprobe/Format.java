
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.DTag;

import java.util.List;

public class Format {
    private final DSection section;

    public Format(DSection section) {
        this.section = section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public String getFilename() {
        return section.getString("filename");
    }

    public int getNbStreams() {
        return section.getInteger("nb_streams");
    }

    public int getNbPrograms() {
        return section.getInteger("nb_programs");
    }

    public String getFormatName() {
        return section.getString("format_name");
    }

    public String getFormatLongName() {
        return section.getString("format_long_name");
    }

    // TODO getter with TimeUnit?
    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    // TODO getter with TimeUnit?
    public Float getDuration() {
        return section.getFloat("duration");
    }

    public Long getSize() {
        return section.getLong("size");
    }

    public Long getBitRate() {
        return section.getLong("bit_rate");
    }

    public Integer getProbeScore() {
        return section.getInteger("probe_score");
    }
}
