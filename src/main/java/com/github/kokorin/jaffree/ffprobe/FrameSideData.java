
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class FrameSideData {
    private final DSection section;

    public FrameSideData(DSection section) {
        this.section = section;
    }

    public String getSideDataType() {
        return section.getString("side_data_type");
    }

    public Integer getSideDataSize() {
        return section.getInteger("side_data_size");
    }

    public String getTimecode() {
        return section.getString("timecode");
    }
}
