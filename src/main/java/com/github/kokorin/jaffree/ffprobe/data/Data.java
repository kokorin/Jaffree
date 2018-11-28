package com.github.kokorin.jaffree.ffprobe.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Data {
    private final Map<String, List<DSection>> sections;

    public Data(Map<String, List<DSection>> sections) {
        this.sections = sections;
    }

    public List<DSection> getSections(String name) {
        List<DSection> result = sections.get(name);
        if (result != null) {
            return result;
        }

        return Collections.emptyList();
    }

    public <T> List<T> getSections(String name, SectionConverter<T> converter) {
        List<T> result = new ArrayList<>();
        for (DSection dSection : getSections(name)) {
            result.add(converter.convert(dSection));
        }
        return result;
    }

    public interface SectionConverter<T> {
        T convert(DSection dSection);
    }
}
