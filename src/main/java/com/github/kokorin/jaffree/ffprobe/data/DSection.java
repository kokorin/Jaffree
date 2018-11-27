package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DSection extends DBase {
    private final Map<String, DTag> tags;
    private final Map<String, List<DSection>> subSections;

    private static final Logger LOGGER = LoggerFactory.getLogger(DSection.class);

    public DSection(Map<String, String> properties, Map<String, DTag> tags, Map<String, List<DSection>> subSections) {
        super(properties);
        this.tags = tags;
        this.subSections = subSections;
    }

    public DTag getTag(String name) {
        DTag result = tags.get(name);
        if (result == null) {
            return DTag.EMPTY;
        }

        return result;
    }

    public List<DSection> getSubSections(String name) {
        List<DSection> result = subSections.get(name);
        if (result == null) {
            return Collections.emptyList();
        }

        return result;
    }
}
