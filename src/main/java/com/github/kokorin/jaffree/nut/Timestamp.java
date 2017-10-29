package com.github.kokorin.jaffree.nut;

import java.util.Objects;

public class Timestamp {
    public final int timebaseId;
    public final long pts;

    public Timestamp(int timebaseId, long pts) {
        this.timebaseId = timebaseId;
        this.pts = pts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timestamp timestamp = (Timestamp) o;
        return timebaseId == timestamp.timebaseId &&
                pts == timestamp.pts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timebaseId, pts);
    }
}
