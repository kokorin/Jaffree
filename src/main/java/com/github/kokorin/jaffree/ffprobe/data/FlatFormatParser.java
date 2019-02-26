package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class FlatFormatParser implements FormatParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFormatParser.class);

    @Override
    public String getFormatName() {
        return "flat";
    }

    @Override
    public Data parse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Iterator<String> lines = new LineIterator(reader);

        Data data = new Data(new TreeMap<String, List<DSection>>(String.CASE_INSENSITIVE_ORDER));

        while (lines.hasNext()) {
            String line = lines.next();

            String[] keyValue = line.split("=", 2);

            if (keyValue.length < 2) {
                LOGGER.warn("Failed to parse line: " + line);
                continue;
            }

            String key = keyValue[0];
            String value = keyValue[1];

            String[] path = key.split("\\.");

        }

        return data;
    }

    private static interface Path {
        Object next(Object prev);

        void set(Object current, String value);
    }

    private static class RootPath implements Path {
        @Override
        public Object next(Object prev) {
            return prev;
        }

        @Override
        public void set(Object current, String value) {
            throw new RuntimeException("Can't set");
        }
    }

    private static class SectionPath implements Path {
        private final String name;
        private final Integer index;

        public SectionPath(String name, Integer index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public Object next(Object prev) {
            List<DSection> dSections = null;

            if (prev instanceof Data) {
                dSections = ((Data) prev).getSections(name);
            } else if (prev instanceof DSection) {
                dSections = ((DSection) prev).getSections(name);
            }

            if (dSections == null) {
                return null;
            }

            while (dSections.size() <= index) {
                dSections.add(new DSection(
                        new HashMap<String, String>(),
                        new TreeMap<String, DTag>(String.CASE_INSENSITIVE_ORDER),
                        new TreeMap<String, List<DSection>>(String.CASE_INSENSITIVE_ORDER)
                ));
            }

            return dSections.get(index);
        }

        @Override
        public void set(Object current, String value) {
            throw new RuntimeException("Can't set");
        }

        public static SectionPath parse(String group, String name, String index) {
            if (!isNumeric(index)) {
                return null;
            }

            return new SectionPath(name, Integer.valueOf(index));
        }
    }

    private static class TagPath implements Path {
        private final String name;

        public TagPath(String name) {
            this.name = name;
        }

        @Override
        public Object next(Object prev) {
            DSection section = (DSection) prev;

            DTag tag = section.getTag(name);
            if (tag == null) {
                tag = new DTag(new HashMap<String, String>());
                section.setTag(name, tag);
            }

            return tag;
        }

        @Override
        public void set(Object current, String value) {
            throw new RuntimeException("Can't set");
        }
    }

    private static class PropertyPath implements Path {
        private final String name;

        public PropertyPath(String name) {
            this.name = name;
        }

        @Override
        public Object next(Object prev) {
            throw new RuntimeException("Cant get next");
        }

        @Override
        public void set(Object current, String value) {
            ((DBase) current).setValue(name, value);
        }

        public static PropertyPath parse(String name) {
            return new PropertyPath(name);
        }
    }

    private static boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            // ignored
        }

        return false;
    }
}
