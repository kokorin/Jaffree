package com.github.kokorin.jaffree;

public enum StreamType {
    /**
     *  Matches all video streams
     */
    ALL_VIDEO("v"),

    /**
     * Only matches video streams which are not attached pictures
     */
    VIDEO("V"),
    AUDIO("a"),
    SUBTITLE("s"),
    DATA("d"),
    ATTACHMENT("t");

    private String code;

    StreamType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
