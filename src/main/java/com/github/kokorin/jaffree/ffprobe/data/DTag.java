package com.github.kokorin.jaffree.ffprobe.data;

import com.github.kokorin.jaffree.ffprobe.Tag;

import java.util.Collections;
import java.util.Map;

public class DTag extends DBase {
    public static final DTag EMPTY = new DTag(Collections.<String, String>emptyMap());

    public DTag(Map<String, String> properties) {
        super(properties);
    }

    public static final KeyValueConverter<Tag> TAG_CONVERTER = new KeyValueConverter<Tag>() {
        @Override
        public Tag convert(String key, String value) {
            return new Tag(key, value);
        }
    };
}
