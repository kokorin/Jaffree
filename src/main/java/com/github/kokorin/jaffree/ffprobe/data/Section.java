package com.github.kokorin.jaffree.ffprobe.data;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Section {
    private final Map<String, String> properties;
    private final Map<String, Map<String, String>> subSections;

    private static final Logger LOGGER = LoggerFactory.getLogger(Section.class);

    public Section(Map<String, String> properties, Map<String, Map<String, String>> subSections) {
        this.properties = properties;
        this.subSections = subSections;
    }

    public <T> T get(String key, ValueConverter<T> converter) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        }

        return converter.convert(value);
    }

    public String getString(String key) {
        return properties.get(key);
    }

    public Long getLong(String key) {
        return get(key, LONG_CONVERTER);
    }

    public Integer getInteger(String key) {
        return get(key, INTEGER_CONVERTER);
    }

    public Float getFloat(String key) {
        return get(key, FLOAT_CONVERTER);
    }

    public StreamType getStreamType(String key) {
        return get(key, STREAM_TYPE_CONVERTER);
    }

    public Rational getRational(String key) {
        return get(key, RATIONAL_CONVERTER);
    }

    public Rational getRatio(String key) {
        return get(key, RATIO_CONVERTER);
    }

    public Section getSubSection(String name) {
        Map<String, String> props = subSections.get(name);
        if (props == null) {
            props = Collections.emptyMap();
        }

        Map<String, Map<String, String>> subs = Collections.emptyMap();

        return new Section(props, subs);
    }

    public <T> List<T> getSubSection(String name, KeyValueConverter<T> converter) {
        List<T> result = new ArrayList<>();

        Map<String, String> props = subSections.get(name);
        if (props == null) {
            props = Collections.emptyMap();
        }

        for (Map.Entry<String, String> entry : props.entrySet()) {
            result.add(converter.convert(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    //
    // Converters
    //

    public interface ValueConverter<T> {
        T convert(String value);
    }

    public interface KeyValueConverter<T> {
        T convert(String key, String value);
    }

    public static final ValueConverter<Long> LONG_CONVERTER = new ValueConverter<Long>() {
        @Override
        public Long convert(String value) {
            return Long.valueOf(value);
        }
    };

    public static final ValueConverter<Integer> INTEGER_CONVERTER = new ValueConverter<Integer>() {
        @Override
        public Integer convert(String value) {
            return Integer.valueOf(value);
        }
    };

    public static final ValueConverter<Float> FLOAT_CONVERTER = new ValueConverter<Float>() {
        @Override
        public Float convert(String value) {
            return Float.valueOf(value);
        }
    };

    public static final ValueConverter<StreamType> STREAM_TYPE_CONVERTER = new ValueConverter<StreamType>() {
        @Override
        public StreamType convert(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }

            try {
                return StreamType.valueOf(value.toUpperCase());
            } catch (Exception e) {
                LOGGER.warn("Failed to parse rational number: " + value, e);
            }

            return null;
        }
    };

    public static final ValueConverter<Rational> RATIONAL_CONVERTER = new RationalConverter();

    public static final ValueConverter<Rational> RATIO_CONVERTER = new RationalConverter(":");

    public static class RationalConverter implements ValueConverter<Rational> {
        private final String delimiter;

        public RationalConverter() {
            this("/");
        }

        public RationalConverter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Rational convert(String value) {
            if (value == null || value.isEmpty() || value.equals("0/0")) {
                return null;
            }

            try {
                return Rational.valueOf(value, delimiter);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse rational number: " + value, e);
            }

            return null;
        }
    }

    public static final KeyValueConverter<Tag> TAG_CONVERTER = new KeyValueConverter<Tag>() {
        @Override
        public Tag convert(String key, String value) {
            return new Tag(key, value);
        }
    };
}
