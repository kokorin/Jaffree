
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class Error {
    private final DSection section;

    public Error(DSection section) {
        this.section = section;
    }

    public int getcode() {
        return section.getInteger("code");
    }

    public String getstring() {
        return section.getString("string");
    }

}
