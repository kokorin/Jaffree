
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class PixelFormatComponent {
    private final DSection section;

    public PixelFormatComponent(DSection section) {
        this.section = section;
    }

    public int getindex() {
        return section.getInteger("index");
    }

    public int getbitDepth() {
        return section.getInteger("bit_depth");
    }
}
