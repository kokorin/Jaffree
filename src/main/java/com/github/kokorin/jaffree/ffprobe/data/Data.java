package com.github.kokorin.jaffree.ffprobe.data;

import java.util.List;
import java.util.Map;

public class Data {
    private final Map<String, List<Section>> sections;

    public Data(Map<String, List<Section>> sections) {
        this.sections = sections;
    }

    public List<Section> getSections(String sectionName) {
        return sections.get(sectionName);
    }
}
