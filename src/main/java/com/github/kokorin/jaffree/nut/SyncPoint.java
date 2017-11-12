package com.github.kokorin.jaffree.nut;

public class SyncPoint {
    public final Timestamp globalKeyPts;
    public final long backKeyPts;
    public final Timestamp transmitTs;

    public SyncPoint(Timestamp globalKeyPts, long backKeyPts) {
        this(globalKeyPts, backKeyPts, null);
    }

    public SyncPoint(Timestamp globalKeyPts, long backKeyPts, Timestamp transmitTs) {
        this.globalKeyPts = globalKeyPts;
        this.backKeyPts = backKeyPts;
        this.transmitTs = transmitTs;
    }
}
