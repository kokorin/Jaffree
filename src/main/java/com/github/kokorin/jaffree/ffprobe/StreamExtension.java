package com.github.kokorin.jaffree.ffprobe;

import java.util.concurrent.TimeUnit;

public class StreamExtension {
    public static Long getStartTime(Stream stream, TimeUnit timeUnit) {
        return fromSeconds(stream.getStartTime(), timeUnit);
    }

    public static Long getDuration(Stream stream, TimeUnit timeUnit) {
        return fromSeconds(stream.getDuration(), timeUnit);
    }

    private static Long fromSeconds(Float seconds, TimeUnit timeUnit) {
        if (seconds == null) {
            return null;
        }

        long millis = (long) (1000 * seconds);
        return timeUnit.convert(millis, TimeUnit.MILLISECONDS);
    }
}
