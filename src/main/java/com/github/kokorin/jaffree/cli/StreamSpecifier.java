package com.github.kokorin.jaffree.cli;

/**
 * Stream specifiers are used to precisely specify which stream(s) a given option belongs to.
 * @see <a href="https://ffmpeg.org/ffprobe.html#toc-Stream-specifiers-1">stream specifiers</a>
 */
public class StreamSpecifier {
    private final String value;

    public StreamSpecifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StreamSpecifier withIndex(int index) {
        return new StreamSpecifier(Integer.toString(index));
    }

    public static StreamSpecifier withType(StreamType type) {
        return new StreamSpecifier(type.code());
    }

    public static StreamSpecifier withTypeAndIndex(StreamType type, int index) {
        return new StreamSpecifier(type.code() + ":" + index);
    }

    public static StreamSpecifier withProgramId(int programId) {
        return new StreamSpecifier("p:" + programId);
    }

    public static StreamSpecifier withProgramIdAndStreamIndex(int programId, int index) {
        return new StreamSpecifier("p:" + programId + ":" + index);
    }

    public static StreamSpecifier withMetadataKey(String key) {
        return new StreamSpecifier("m:" + key);
    }

    public static StreamSpecifier withMetadataKeyAndValue(String key, String value) {
        return new StreamSpecifier("m:" + key + ":" + value);
    }

    public static StreamSpecifier withUsableConfiguration() {
        return new StreamSpecifier("u");
    }
}
