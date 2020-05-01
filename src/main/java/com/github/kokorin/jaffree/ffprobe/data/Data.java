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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Data structure which represents whole ffprobe output.
 */
public class Data {
    private final DSection sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(Data.class);

    /**
     * Creates {@link Data}.
     *
     * @param sections sections
     */
    public Data(final Map<String, List<DSection>> sections) {
        this.sections = new DSection(null, null, sections);
    }

    /**
     * Returns root-level tag.
     *
     * @param name name
     * @return tag
     */
    public DTag getTag(final String name) {
        return sections.getTag(name);
    }

    /**
     * Returns root-level sections by name.
     *
     * @param name name
     * @return sections
     */
    public List<DSection> getSections(final String name) {
        return sections.getSections(name);
    }

    /**
     * Returns single root-level section its name.
     *
     * @param name name
     * @return sections
     */
    public DSection getSection(final String name) {
        return sections.getSection(name);
    }

    /**
     * Handy method which returns root-level sections of the specified name converted to T type.
     *
     * @param name      name
     * @param converter converter
     * @param <T>       result type
     * @return converted sections
     */
    public <T> List<T> getSections(final String name,
                                   final DSection.SectionConverter<T> converter) {
        return sections.getSections(name, converter);
    }
}
