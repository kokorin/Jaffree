
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class Log {
    private final DSection section;

    public Log(DSection section) {
        this.section = section;
    }

    public String getContext() {
        return section.getString("context");
    }

    public Integer getLevel() {
        return section.getInteger("level");
    }

    public Integer getCategory() {
        return section.getInteger("category");
    }

    public String getParentContext() {
        return section.getString("parent_context");
    }

    public Integer getParentCategory() {
        return section.getInteger("parent_category");
    }

    public String getMessage() {
        return section.getString("message");
    }
}
