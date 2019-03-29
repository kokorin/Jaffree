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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DSection extends DBase {
    private final Map<String, DTag> tags;
    private final Map<String, List<DSection>> sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(DSection.class);

    public DSection(Map<String, String> properties, Map<String, DTag> tags, Map<String, List<DSection>> sections) {
        super(properties);
        this.tags = tags;
        this.sections = sections;
    }

    public DTag getTag(String name) {
        DTag result = tags.get(name);
        if (result == null) {
            return DTag.EMPTY;
        }

        return result;
    }

    public DTag getTag(String ...names) {
        for (String name : names) {
            DTag result = tags.get(name);
            if (result != null) {
                return result;
            }
        }

        return DTag.EMPTY;
    }

    public boolean hasTag(String name) {
        return tags.containsKey(name);
    }

    public void setTag(String name, DTag tag) {
        tags.put(name, tag);
    }

    public List<DSection> getSections(String name) {
        List<DSection> result = sections.get(name);

        if (result == null) {
            result = new ArrayList<>();
            sections.put(name, result);
        }

        return result;
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
