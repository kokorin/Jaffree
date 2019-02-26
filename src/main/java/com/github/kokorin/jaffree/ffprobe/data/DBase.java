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

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBase {
    private final Map<String, String> properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(DBase.class);

    public DBase(Map<String, String> properties) {
        this.properties = properties;
    }

    public <T> T getValue(String key, ValueConverter<T> converter) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        }

        return converter.convert(value);
    }

    public void setValue(String key, String value) {
        properties.put(key, value);
    }

    public <T> List<T> getValues(KeyValueConverter<T> converter) {
        List<T> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            result.add(converter.convert(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    public String getString(String key) {
        return properties.get(key);
    }

    public Long getLong(String key) {
        return getValue(key, LONG_CONVERTER);
    }

    public Integer getInteger(String key) {
        return getValue(key, INTEGER_CONVERTER);
    }

    public Float getFloat(String key) {
        return getValue(key, FLOAT_CONVERTER);
    }

    public StreamType getStreamType(String key) {
        return getValue(key, STREAM_TYPE_CONVERTER);
    }

    public Rational getRational(String key) {
        return getValue(key, RATIONAL_CONVERTER);
    }

    public Rational getRatio(String key) {
        return getValue(key, RATIO_CONVERTER);
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
            if (value == null || value.isEmpty() || value.equals("N/A")) {
                return null;
            }

            try {
                return Long.valueOf(value);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse long number: " + value, e);
            }

            return null;
        }
    };

    public static final ValueConverter<Integer> INTEGER_CONVERTER = new ValueConverter<Integer>() {
        @Override
        public Integer convert(String value) {
            if (value == null || value.isEmpty() || value.equals("N/A")) {
                return null;
            }

            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse integer number: " + value, e);
            }

            return null;
        }
    };

    public static final ValueConverter<Float> FLOAT_CONVERTER = new ValueConverter<Float>() {
        @Override
        public Float convert(String value) {
            if (value == null || value.isEmpty() || value.equals("N/A")) {
                return null;
            }

            try {
                return Float.valueOf(value);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse float number: " + value, e);
            }

            return null;
        }
    };

    public static final ValueConverter<StreamType> STREAM_TYPE_CONVERTER = new ValueConverter<StreamType>() {
        @Override
        public StreamType convert(String value) {
            if (value == null || value.isEmpty() || value.equals("N/A")) {
                return null;
            }

            try {
                return StreamType.valueOf(value.toUpperCase());
            } catch (Exception e) {
                LOGGER.warn("Failed to parse StreamType: " + value, e);
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
            if (value == null || value.isEmpty() || value.equals("0/0") || value.equals("N/A")) {
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
}
