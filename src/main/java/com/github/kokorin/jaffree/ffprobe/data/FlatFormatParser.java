/*
 *    Copyright 2019-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.util.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ffprobe flat format output parser.
 *
 * @deprecated use {@link JsonFormatParser}
 */
@Deprecated
public class FlatFormatParser implements FormatParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFormatParser.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormatName() {
        return "flat";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProbeData parse(final InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Iterator<String> lines = new LineIterator(reader);

        // Intentionally make Map which ignores case of key
        TreeMap<String, Object> data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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

        return new ProbeDataMap(data);
    }

    /**
     * Parses and sets key-value to specified data.
     *
     * @param data  data to add key-value to
     * @param key   key
     * @param value value
     * @return true if parsed and set
     */
    protected boolean setKeyValue(final TreeMap<String, Object> data, final String key,
                                  final String value) {
        String[] pathStr = key.split("\\.");
        List<Path> path = new ArrayList<>();

        int inc;
        for (int i = 0; i < pathStr.length; i += inc) {
            Path step = null;
            inc = 1;

            if (i + 2 < pathStr.length) {
                step = SectionPath.parse(pathStr[i], pathStr[i + 1], pathStr[i + 2]);
                if (step != null) {
                    inc = 1 + 1 + 1;
                }
            }

            if (step == null && i == pathStr.length - 1) {
                step = new PropertyPath(pathStr[i]);
            }

            if (step == null && i <= pathStr.length - 2) {
                step = new TagPath(pathStr[i]);
            }

            if (step == null) {
                LOGGER.warn("Failed to parse path: {}", key);
            }

            path.add(step);
        }

        if (path.isEmpty()) {
            LOGGER.warn("Parsed path is empty: {}", key);
            return false;
        }

        TreeMap<String, Object> current = data;
        Path step = null;

        for (Path p : path) {
            step = p;
            current = step.next(current);
        }

        String fixedValue = fixValue(value);

        return step.set(current, fixedValue);
    }

    /**
     * Removes starting-ending quotes and back slashes form value.
     *
     * @param value value
     * @return un-escaped value
     */
    protected String fixValue(final String value) {
        String unquotedValue = value;
        if (value.startsWith("\"") && value.endsWith("\"")) {
            unquotedValue = value.substring(1, value.length() - 1);
        }

        return unquotedValue.replaceAll("\\\\n", "\n");
    }

    private interface Path {
        TreeMap<String, Object> next(TreeMap<String, Object> prev);

        boolean set(TreeMap<String, Object> current, String value);
    }

    private static class SectionPath implements Path {
        private final String section;
        private final String element;
        private final Integer index;

        SectionPath(final String section, final String element, final Integer index) {
            this.section = section;
            this.element = element;
            this.index = index;
        }

        @Override
        public TreeMap<String, Object> next(final TreeMap<String, Object> prev) {
            @SuppressWarnings("unchecked")
            List<TreeMap<String, Object>> valueList =
                    (List<TreeMap<String, Object>>) prev.get(section);
            if (valueList == null) {
                valueList = new ArrayList<>();
                prev.put(section, valueList);
            }

            Object lastElement = prev.get("_last_element");
            Object lastIndex = prev.get("_last_index");

            TreeMap<String, Object> result;
            if (element.equals(lastElement) && index.equals(lastIndex)) {
                result = valueList.get(valueList.size() - 1);
            } else {
                result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                result.put("type", element);
                valueList.add(result);
                prev.put("_last_element", element);
                prev.put("_last_index", index);
            }

            return result;
        }

        @Override
        public boolean set(final TreeMap<String, Object> current, final String value) {
            return false;
        }

        public static SectionPath parse(final String group, final String name, final String index) {
            if (!isNumeric(index)) {
                return null;
            }

            return new SectionPath(group, name, Integer.valueOf(index));
        }
    }

    private static class TagPath implements Path {
        private final String name;

        TagPath(final String name) {
            this.name = name;
        }

        @Override
        public TreeMap<String, Object> next(final TreeMap<String, Object> prev) {
            @SuppressWarnings("unchecked")
            TreeMap<String, Object> result = (TreeMap<String, Object>) prev.get(name);
            if (result == null) {
                result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                prev.put(name, result);
            }

            return result;
        }

        @Override
        public boolean set(final TreeMap<String, Object> current, final String value) {
            return false;
        }
    }

    private static class PropertyPath implements Path {
        private final String name;

        PropertyPath(final String name) {
            this.name = name;
        }

        @Override
        public TreeMap<String, Object> next(final TreeMap<String, Object> prev) {
            return prev;
        }

        @Override
        public boolean set(final TreeMap<String, Object> current, final String value) {
            if (current != null) {
                current.put(name, value);
                return true;
            }
            return false;
        }

        public static PropertyPath parse(final String name) {
            return new PropertyPath(name);
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private static boolean isNumeric(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            // ignored
        }

        return false;
    }

    /**
     * {@link java.util.Map}-based implementation of {@link ProbeData}.
     */
    public static final class ProbeDataMap extends AbstractProbeData implements ProbeData {
        private final Map<String, Object> data;

        /**
         * Creates ProbeDataMap.
         * @param data data map
         */
        public ProbeDataMap(final Map<String, Object> data) {
            this.data = data;
        }

        @Override
        public Object getValue(final String name) {
            return data.get(name);
        }

        @Override
        @SuppressWarnings("unchecked")
        public ProbeData getSubData(final String name) {
            Object value = data.get(name);
            if (value == null) {
                return null;
            }
            if (!(value instanceof Map)) {
                throw new RuntimeException("Not a Map: " + value);
            }
            return new ProbeDataMap((Map<String, Object>) value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<ProbeData> getSubDataList(final String name) {
            Object value = data.get(name);
            if (value == null) {
                return null;
            }
            if (!(value instanceof List)) {
                throw new RuntimeException("Not a List: " + value);
            }
            List<?> valueList = (List<?>) value;
            List<ProbeData> result = new ArrayList<>(valueList.size());
            for (Object item : valueList) {
                if (!(item instanceof Map)) {
                    throw new RuntimeException("No a Map: " + item);
                }
                result.add(new ProbeDataMap((Map<String, Object>) item));
            }
            return result;
        }
    }
}
