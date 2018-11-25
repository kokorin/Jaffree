package com.github.kokorin.jaffree.ffprobe.data;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
public class Section {
    private final Map<String, String> properties;
    private final Map<String, Map<String, String>> sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(Section.class);

    public Section(Map<String, String> properties, Map<String, Map<String, String>> sections) {
        this.properties = properties;
        this.sections = sections;
    }

    public <T> T get(String key, Converter<T> converter) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        }

        return converter.fromString(value);
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

    public Map<String, String> getSections(String name) {
        return sections.get(name);
    }

    //
    // Converters
    //

    public static interface Converter<T> {
        T fromString(String value);
    }

    public static final Converter<Long> LONG_CONVERTER = new Converter<Long>() {
        @Override
        public Long fromString(String value) {
            return Long.valueOf(value);
        }
    };

    public static final Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {
        @Override
        public Integer fromString(String value) {
            return Integer.valueOf(value);
        }
    };

    public static final Converter<Float> FLOAT_CONVERTER = new Converter<Float>() {
        @Override
        public Float fromString(String value) {
            return Float.valueOf(value);
        }
    };

    public static final Converter<StreamType> STREAM_TYPE_CONVERTER = new Converter<StreamType>() {
        @Override
        public StreamType fromString(String value) {
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

    public static final Converter<Rational> RATIONAL_CONVERTER = new RationalConverter();

    public static final Converter<Rational> RATIO_CONVERTER = new RationalConverter(":");

    public static class RationalConverter implements Converter<Rational> {
        private final String delimiter;

        public RationalConverter() {
            this("/");
        }

        public RationalConverter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Rational fromString(String value) {
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
    };
}
