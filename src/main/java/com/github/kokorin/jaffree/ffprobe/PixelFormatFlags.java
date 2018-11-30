
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DTag;

public class PixelFormatFlags {
    private final DTag tag;

    public PixelFormatFlags(DTag tag) {
        this.tag = tag;
    }

    public int getBigEndian() {
        return tag.getInteger("big_endian");
    }

    public int getPalette() {
        return tag.getInteger("palette");
    }

    public int getBitstream() {
        return tag.getInteger("bitstream");
    }

    public int getHwaccel() {
        return tag.getInteger("hwaccel");
    }

    public int getPlanar() {
        return tag.getInteger("planar");
    }

    public int getRgb() {
        return tag.getInteger("rgb");
    }

    public int getPseudopal() {
        return tag.getInteger("pseudopal");
    }

    public int getAlpha() {
        return tag.getInteger("alpha");
    }
}
