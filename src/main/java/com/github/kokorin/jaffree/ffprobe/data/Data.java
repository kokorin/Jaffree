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
