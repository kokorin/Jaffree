
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class ProgramVersion {
    private final DSection section;

    public ProgramVersion(DSection section) {
        this.section = section;
    }

    public String getVersion() {
        return section.getString("version");
    }

    public String getCopyright() {
        return section.getString("copyright");
    }

    public String getBuildDate() {
        return section.getString("build_date");
    }

    public String getBuildTime() {
        return section.getString("build_time");
    }

    public String getCompilerIdent() {
        return section.getString("compiler_ident");
    }

    public String getConfiguration() {
        return section.getString("configuration");
    }
}
