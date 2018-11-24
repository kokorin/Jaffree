package com.github.kokorin.jaffree.ffprobe.data;

import java.util.*;

public class DataParser {
    private Map<String, List<Section>> result = new HashMap<>();

    // State
    private String sectionName = null;
    private Map<String, String> properties = null;
    private Map<String, Map<String, String>> subSections = null;

    public void parseLine(String line) {
        if (line.startsWith("[/") && line.endsWith("]")) {
            String name  = line.substring(2, line.length() - 1);
            sectionEnd(name);
        } else if (line.startsWith("[") && line.endsWith("]")) {
            String name  = line.substring(1, line.length() - 1);
            sectionStart(name);
        } else {
            String[] keyValue = line.split("=");
            if (keyValue.length  != 2) {
                throw new RuntimeException("key=value was expected but got: " + line);
            }
            String key = keyValue[0];
            String value = keyValue[1];
            if (!key.contains(":")) {
                property(key, value);
            } else {
                String[] sectionKey = key.split(":");
                if (sectionKey.length != 2) {
                    throw new RuntimeException("Wrong subsection property format: " + line);
                }

                String section = sectionKey[0];
                key = sectionKey[1];

                subSectionProperty(section, key, value);
            }
        }

    }

    public void sectionStart(String name) {
        if (sectionName != null) {
            throw new RuntimeException("Unexpected start of section " + name);
        }

        sectionName = name;
        properties = new HashMap<>();
        subSections = new HashMap<>();
    }

    public void sectionEnd(String name) {
        if (sectionName == null || !sectionName.equals(name)) {
            throw new RuntimeException("Expecting end of " + sectionName + " but found " + name);
        }

        List<Section> data = result.get(sectionName);
        if (data == null) {
            data = new ArrayList<>();
            result.put(sectionName, data);
        }

        data.add(new Section(properties, subSections));

        sectionName = null;
        properties = null;
        subSections = null;
    }

    public void property(String key, String value) {
        properties.put(key, value);
    }

    public void subSectionProperty(String section, String key, String value) {
        Map<String, String> subSection = subSections.get(section);
        if (subSection == null) {
            subSection = new HashMap<>();
            subSections.put(section, subSection);
        }

        subSection.put(key, value);
    }

    public Data getResult() {
        if (sectionName != null) {
            throw new RuntimeException();
        }

        return new Data(result);
    }

    public static Data parse(Iterator<String> lines) {
        DataParser parser = new DataParser();

        while (lines.hasNext()) {
            String line = lines.next();
            parser.parseLine(line);
        }

        return parser.getResult();
    }
}
