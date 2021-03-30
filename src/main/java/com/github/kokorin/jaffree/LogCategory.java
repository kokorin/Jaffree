package com.github.kokorin.jaffree;

public enum LogCategory {
    NA(0),
    INPUT(1),
    OUTPUT(2),
    MUXER(3),
    DEMUXER(4),
    ENCODER(5),
    DECODER(6),
    FILTER(7),
    BITSTREAM_FILTER(8),
    SWSCALER(9),
    SWRESAMPLER(10),
    DEVICE_VIDEO_OUTPUT(40),
    DEVICE_VIDEO_INPUT(41),
    DEVICE_AUDIO_OUTPUT(42),
    DEVICE_AUDIO_INPUT(43),
    DEVICE_OUTPUT(44),
    DEVICE_INPUT(45),
    NB(46);

    private final int code;

    LogCategory(int code) {
        this.code = code;
    }

    public static LogCategory fromCode(int code) {
        for (LogCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        return null;
    }
}
