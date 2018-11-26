
package com.github.kokorin.jaffree.ffprobe;

public class Tag {
    private final String key;
    private final String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
