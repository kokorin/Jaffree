/*
 *    Copyright  2019 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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

        // Intentionally make Map which ignores case of key
        Data data = new Data(new TreeMap<String, List<DSection>>(String.CASE_INSENSITIVE_ORDER));

        while (lines.hasNext()) {
            String line = lines.next();

            try {
                String[] keyValue = line.split("=", 2);

                if (keyValue.length < 2) {
                    LOGGER.warn("Failed to parse line: {}", line);
                    continue;
                }

                String key = keyValue[0];
                String value = keyValue[1];

                boolean success = setKeyValue(data, key, value);

                if (!success) {
                    LOGGER.warn("Failed to set value: {}", key);
                    continue;
                }

                LOGGER.debug("Parsed: {} = {}", key, value);
            } catch (Exception e) {
                LOGGER.warn("Exception during parsing, ignored: {}", e.getMessage());
            }
        }

        return data;
    }

    protected boolean setKeyValue(Data data, String key, String value) {
        String[] pathStr = key.split("\\.");
        List<Path> path = new ArrayList<>();

        for (int i = 0; i < pathStr.length; ) {
            Path step = null;
            int inc = 1;

            if (i + 2 < pathStr.length) {
                step = SectionPath.parse(pathStr[i], pathStr[i + 1], pathStr[i + 2]);
                if (step != null) {
                    inc = 3;
                }
            }

            if (step == null && i == 0) {
                //force section parsing
                step = new SectionPath(pathStr[i], 0);
            }

            if (step == null && i == pathStr.length - 2) {
                step = new TagPath(pathStr[i]);
            }

            if (step == null && i == pathStr.length - 1) {
                step = new PropertyPath(pathStr[i]);
            }

            if (step == null) {
                LOGGER.warn("Failed to parse path: {}", key);
            }

            path.add(step);
            i += inc;
        }

        if (path.isEmpty()) {
            LOGGER.warn("Parsed path is empty: {}", key);
            return false;
        }

        Object current = data;
        Path step = null;

        for (Path p : path) {
            step = p;
            current = step.next(current);
        }

        value = fixValue(value);

        return step.set(current, value);
    }

    protected String fixValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return value.replaceAll("\\\\n", "\n");
    }

    private static interface Path {
        Object next(Object prev);

        boolean set(Object current, String value);
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
                // Intentionally make Maps which ignore case of key
                dSections.add(new DSection(
                        new HashMap<String, String>(),
                        new TreeMap<String, DTag>(String.CASE_INSENSITIVE_ORDER),
                        new TreeMap<String, List<DSection>>(String.CASE_INSENSITIVE_ORDER)
                ));
            }

            return dSections.get(index);
        }

        @Override
        public boolean set(Object current, String value) {
            return false;
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
            if (!(prev instanceof DSection)) {
                return null;
            }

            DSection section = (DSection) prev;

            if (!section.hasTag(name)) {
                DTag tag = new DTag(new HashMap<String, String>());
                section.setTag(name, tag);
            }

            return section.getTag(name);
        }

        @Override
        public boolean set(Object current, String value) {
            return false;
        }
    }

    private static class PropertyPath implements Path {
        private final String name;

        public PropertyPath(String name) {
            this.name = name;
        }

        @Override
        public Object next(Object prev) {
            return prev;
        }

        @Override
        public boolean set(Object current, String value) {
            if (current instanceof DBase) {
                ((DBase) current).setValue(name, value);
                return true;
            }
            return false;
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
