
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class LibraryVersion {
    private final DSection section;

    public LibraryVersion(DSection section) {
        this.section = section;
    }

    public String getName() {
        return section.getString("name");
    }

    public int getMajor() {
        return section.getInteger("major");
    }

    public int getMinor() {
        return section.getInteger("minor");
    }

    public int getMicro() {
        return section.getInteger("micro");
    }

    public int getVersion() {
        return section.getInteger("version");
    }

    public String getIdent() {
        return section.getString("ident");
    }
}
