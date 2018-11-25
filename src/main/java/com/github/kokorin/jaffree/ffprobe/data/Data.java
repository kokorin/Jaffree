package com.github.kokorin.jaffree.ffprobe.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Data {
    private final Map<String, List<Section>> sections;

    public Data(Map<String, List<Section>> sections) {
        this.sections = sections;
    }

    public List<Section> getSections(String sectionName) {
        List<Section> result = sections.get(sectionName);
        if (result != null) {
            return result;
        }

        return Collections.emptyList();
    }
}
