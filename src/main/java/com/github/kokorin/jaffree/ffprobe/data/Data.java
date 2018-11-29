package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Data {
    private final Map<String, List<DSection>> sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(Data.class);

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

    public DSection getSection(String name) {
        List<DSection> sections = getSections(name);
        if (sections.isEmpty()) {
            return null;
        }

        if (sections.size() > 1) {
            LOGGER.warn("Single section requested: {}, but there are {} sections with the same name");
        }

        return sections.get(0);
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
