
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class PacketSideData {
    private final DSection section;

    public PacketSideData(DSection section) {
        this.section = section;
    }

    public String getSideDataType() {
        return section.getString("side_data_type");
    }

    public Integer getSideDataSize() {
        return section.getInteger("side_data_size");
    }

    public String getDisplayMatrix() {
        return section.getString("displaymatrix");
    }

    public Integer getRotation() {
        return section.getInteger("rotation");
    }
}
