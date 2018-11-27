package com.github.kokorin.jaffree.ffprobe.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Data {
    private final Map<String, List<DSection>> sections;

    public Data(Map<String, List<DSection>> sections) {
        this.sections = sections;
    }

    public List<DSection> getSections(String sectionName) {
        List<DSection> result = sections.get(sectionName);
        if (result != null) {
            return result;
        }

        return Collections.emptyList();
    }
}
