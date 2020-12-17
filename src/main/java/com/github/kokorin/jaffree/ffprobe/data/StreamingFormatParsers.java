package com.github.kokorin.jaffree.ffprobe.data;

public final class StreamingFormatParsers {
    private StreamingFormatParsers() {
    }
    
    public static StreamingFormatParser createDefault() {
        return new StreamingDefaultFormatParser();
    }
    
    public static StreamingFormatParser createFlat() {
        return new StreamingFlatFormatParser();
    }
}
