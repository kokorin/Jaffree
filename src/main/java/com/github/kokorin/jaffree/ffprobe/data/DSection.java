/*
 *    Copyright  2018 Denis Kokorin
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

import com.github.kokorin.jaffree.ffprobe.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data structure which represents single section in ffprobe output.
 * <p>
 * Data section can contain all types of data structures: properties, tags and sub-sections
 */
public class DSection extends DBase {
    private final Map<String, DTag> tags;
    private final Map<String, List<DSection>> sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(DSection.class);

    /**
     * Creates {@link DSection}.
     *
     * @param properties properties
     * @param tags       tags
     * @param sections   sections
     */
    public DSection(final Map<String, String> properties,
                    final Map<String, DTag> tags,
                    final Map<String, List<DSection>> sections) {
        super(properties);
        this.tags = tags;
        this.sections = sections;
    }

    /**
     * Returns tag by its name.
     *
     * @param name tag name
     * @return tag
     */
    public DTag getTag(final String name) {
        DTag result = tags.get(name);
        if (result == null) {
            return DTag.EMPTY;
        }

        return result;
    }

    /**
     * Returns tag by any of the specified names in the fallback manner.
     * <p>
     * This method is designed for cases when different implementations of {@link FormatParser}
     * produce different names for the same tag.
     *
     * @param names tag names
     * @return tag
     */
    public DTag getTag(final String... names) {
        for (String name : names) {
            DTag result = tags.get(name);
            if (result != null) {
                return result;
            }
        }

        return DTag.EMPTY;
    }

    /**
     * Checks if tag is present.
     *
     * @param name name
     * @return tag presence
     */
    public boolean hasTag(final String name) {
        return tags.containsKey(name);
    }

    // TODO: make DSection immutable
    @SuppressWarnings("checkstyle:designforextension")
    void setTag(final String name, final DTag tag) {
        tags.put(name, tag);
    }

    /**
     * Returns sub-sections by name.
     *
     * @param name name
     * @return sub-sections
     */
    //TODO: FlatFormatParser uses this method to put extra sections
    public List<DSection> getSections(final String name) {
        List<DSection> result = sections.get(name);

        if (result == null) {
            // TODO: Collections.emptyList() ?
            result = new ArrayList<>();
            sections.put(name, result);
        }

        return result;
    }

    /**
     * Returns single subsection by name.
     *
     * @param name name
     * @return sub-section
     */
    public DSection getSection(final String name) {
        List<DSection> subSections = getSections(name);
        if (subSections.isEmpty()) {
            return null;
        }

        if (subSections.size() > 1) {
            LOGGER.warn("Single section requested: {}, "
                    + "but there are {} sections with the same name", name, subSections.size());
        }

        return subSections.get(0);
    }

    /**
     * Handy method which returns sub-sections of the specified name converted to T type.
     *
     * @param name      name
     * @param converter converter
     * @param <T>       result type
     * @return converted sub-sections
     */
    public <T> List<T> getSections(final String name, final SectionConverter<T> converter) {
        List<T> result = new ArrayList<>();
        for (DSection dSection : getSections(name)) {
            result.add(converter.convert(dSection));
        }
        return result;
    }

    /**
     * Represents a converter which is used to convert requested DSection to T type.
     *
     * @param <T> type to convert to
     */
    public interface SectionConverter<T> {

        /**
         * Converts {@link DSection} to T type.
         * @param dSection dSection
         * @return converted dSection
         */
        T convert(DSection dSection);
    }

    public static final SectionConverter<Stream> STREAM_CONVERTER = new SectionConverter<Stream>() {
        @Override
        public Stream convert(DSection dSection) {
            return new Stream(dSection);
        }
    };
}
