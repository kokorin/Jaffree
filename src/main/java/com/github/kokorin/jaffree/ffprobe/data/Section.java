package com.github.kokorin.jaffree.ffprobe.data;

import java.util.Map;

public class Section {
    private final Map<String, String> properties;
    private final Map<String, Map<String, String>> sections;

    public Section(Map<String, String> properties, Map<String, Map<String, String>> sections) {
        this.properties = properties;
        this.sections = sections;
    }

    public String getString(String key) {
        return properties.get(key);
    }

    public Long getLong(String key) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return null;
        }

        return Long.parseLong(value);
    }

    public Map<String, String> getSections(String name) {
        return sections.get(name);
    }
}
