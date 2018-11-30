package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Data {
    private final DSection sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(Data.class);

    public Data(Map<String, List<DSection>> sections) {
        this.sections = new DSection(null, null, sections);
    }

    public DTag getTag(String name) {
        return sections.getTag(name);
    }

    public List<DSection> getSections(String name) {
        return sections.getSections(name);
    }

    public DSection getSection(String name) {
        return sections.getSection(name);
    }

    public <T> List<T> getSections(String name, DSection.SectionConverter<T> converter) {
        return sections.getSections(name, converter);
    }
}
