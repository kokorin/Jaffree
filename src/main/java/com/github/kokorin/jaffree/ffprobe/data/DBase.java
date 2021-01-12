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

/**
 * Abstract base class for ffprobe output data structures.
 */
public abstract class DBase {
    private final Map<String, String> properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(DBase.class);

    /**
     * Creates {@link DBase} with passed in properties.
     *
     * @param properties properties
     */
    public DBase(final Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Handy method to get single value of specific type.
     *
     * @param key       key name
     * @param converter converter to use
     * @param <T>       return type
     * @return value for key
     */
    public <T> T getValue(final String key, final ValueConverter<T> converter) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        }

        return converter.convert(value);
    }

    // TODO: make DBase immutable
    @SuppressWarnings("checkstyle:designforextension")
    void setValue(final String key, final String value) {
        properties.put(key, value);
    }

    /**
     * Handy method to get all properties of specific type.
     *
     * @param converter converter to use
     * @param <T>       return type
     * @return values
     */
    public <T> List<T> getValues(final KeyValueConverter<T> converter) {
        List<T> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            result.add(converter.convert(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    /**
     * Returns string value for specified key.
     *
     * @param key key
     * @return value
     */
    public String getString(final String key) {
        return properties.get(key);
    }


    /**
     * Returns long value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    public Long getLong(final String key) {
        return getValue(key, LONG_CONVERTER);
    }

    /**
     * Returns integer value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    public Integer getInteger(final String key) {
        return getValue(key, INTEGER_CONVERTER);
    }

    /**
     * Returns float value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    public Float getFloat(final String key) {
        return getValue(key, FLOAT_CONVERTER);
    }

    /**
     * Returns StreamType value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    // TODO: check if it should be here
    public StreamType getStreamType(final String key) {
        return getValue(key, STREAM_TYPE_CONVERTER);
    }

    /**
     * Returns rational value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    public Rational getRational(final String key) {
        return getValue(key, RATIONAL_CONVERTER);
    }

    /**
     * Returns ratio value for specified key (using default converter).
     * <p>
     * Note: usually ration is represented by 2 digits separated by : (colon)
     *
     * @param key key
     * @return value
     */
    public Rational getRatio(final String key) {
        return getValue(key, RATIO_CONVERTER);
    }


    //
    // Converters
    //

    /**
     * Represents a converter which is used to convert requested value to T type.
     *
     * @param <T> type to convert to
     */
    public interface ValueConverter<T> {
        /**
         * Converts passed in {@link String} value to T type.
         *
         * @param value value
         * @return converted value
         */
        T convert(String value);
    }

    /**
     * Represents a converter which is used to convert key-value pair to T type.
     *
     * @param <T> type to convert to
     */
    public interface KeyValueConverter<T> {
        /**
         * Converts passed in key-value pair to T type.
         *
         * @param key   key
         * @param value value
         * @return converted value
         */
        T convert(String key, String value);
    }

    public static final ValueConverter<Long> LONG_CONVERTER = new ValueConverter<Long>() {
        @Override
        public Long convert(final String value) {
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
        public Integer convert(final String value) {
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
        public Float convert(final String value) {
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

    public static final ValueConverter<StreamType> STREAM_TYPE_CONVERTER =
            new ValueConverter<StreamType>() {
                @Override
                public StreamType convert(final String value) {
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

    public static final ValueConverter<Rational> RATIONAL_CONVERTER = new RationalConverter("/");

    public static final ValueConverter<Rational> RATIO_CONVERTER = new RationalConverter(":");

    private static class RationalConverter implements ValueConverter<Rational> {
        private final String delimiter;

        RationalConverter(final String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Rational convert(final String value) {
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
