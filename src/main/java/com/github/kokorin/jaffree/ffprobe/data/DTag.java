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

import com.github.kokorin.jaffree.ffprobe.Tag;

import java.util.Collections;
import java.util.Map;

/**
 * Data structure which represents single tag in ffprobe output.
 * <p>
 * Tag can contain only properties
 */
public class DTag extends DBase {
    public static final DTag EMPTY = new DTag(Collections.<String, String>emptyMap());

    /**
     * Creates {@link DTag}.
     * @param properties tag properties
     */
    public DTag(final Map<String, String> properties) {
        super(properties);
    }

    // TODO: move to com.github.kokorin.jaffree.ffprobe.Tag
    public static final KeyValueConverter<Tag> TAG_CONVERTER = new KeyValueConverter<Tag>() {
        @Override
        public Tag convert(final String key, final String value) {
            return new Tag(key, value);
        }
    };
}
