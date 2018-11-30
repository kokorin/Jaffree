
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class Subtitle {
    private final DSection section;

    public Subtitle(DSection section) {
        this.section = section;
    }

    public StreamType getMediaType() {
        return section.getStreamType("media_type");
    }

    public Long getPts() {
        return section.getLong("pts");
    }

    public Float getPtsTime() {
        return section.getFloat("pts_time");
    }

    public Integer getFormat() {
        return section.getInteger("format");
    }

    public Integer getStartDisplayTime() {
        return section.getInteger("start_display_time");
    }

    public Integer getEndDisplayTime() {
        return section.getInteger("end_display_time");
    }

    public Integer getNumRects() {
        return section.getInteger("num_rects");
    }
}
