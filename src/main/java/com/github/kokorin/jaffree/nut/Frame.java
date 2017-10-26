package com.github.kokorin.jaffree.nut;

public class Frame {
    public final int streamId;
    public final long pts;
    public final byte[] data;
    public final DataItem[] sideData;
    public final DataItem[] metaData;

    public Frame(int streamId, long pts, byte[] data, DataItem[] sideData, DataItem[] metaData) {
        this.streamId = streamId;
        this.pts = pts;
        this.data = data;
        this.sideData = sideData;
        this.metaData = metaData;
    }
}
