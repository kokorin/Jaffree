/*
 *    Copyright 2021 Denis Kokorin
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

/**
 * Abstract {@link ProbeData} implementation with all common methods implemented.
 * <p>
 * Data-specific methods are left for subclasses.
 */
public abstract class AbstractProbeData implements ProbeData {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProbeData.class);

    @Override
    public String getString(final String key) {
        return getValue(key, STRING_CONVERTER);
    }

    /**
     * Returns long value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    @Override
    public Long getLong(final String key) {
        return getValue(key, LONG_CONVERTER);
    }

    /**
     * Returns integer value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    @Override
    public Integer getInteger(final String key) {
        return getValue(key, INTEGER_CONVERTER);
    }

    /**
     * Returns float value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    @Override
    public Float getFloat(final String key) {
        return getValue(key, FLOAT_CONVERTER);
    }

    /**
     * Returns double value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    @Override
    public Double getDouble(final String key) {
        return getValue(key, DOUBLE_CONVERTER);
    }

    /**
     * Returns StreamType value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    // TODO: check if it should be here
    @Override
    public StreamType getStreamType(final String key) {
        return getValue(key, STREAM_TYPE_CONVERTER);
    }

    /**
     * Returns rational value for specified key (using default converter).
     *
     * @param key key
     * @return value
     */
    @Override
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
    @Override
    public Rational getRatio(final String key) {
        return getValue(key, RATIO_CONVERTER);
    }

    @Override
    public <T> T getValue(String name, ValueConverter<T> converter) {
        Object value = getValue(name);
        if (value == null) {
            return null;
        }
        return converter.convert(value);
    }

    @Override
    public final <T> T getSubData(String name, ProbeDataConverter<T> converter) {
        ProbeData data = getSubData(name);
        if (data == null) {
            return null;
        }
        return converter.convert(data);
    }

    @Override
    public final <T> List<T> getSubDataList(String name, ProbeDataConverter<T> converter) {
        List<ProbeData> dataList = getSubDataList(name);
        if (dataList == null) {
            return null;
        }
        List<T> result = new ArrayList<>(dataList.size());
        for (ProbeData data : dataList) {
            result.add(converter.convert(data));
        }
        return result;
    }

    private static final ValueConverter<String> STRING_CONVERTER =
            new ValueConverter<String>() {
                @Override
                public String convert(final Object value) {
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }
            };

    private static final ValueConverter<Long> LONG_CONVERTER =
            new ValueConverter<Long>() {
                @Override
                public Long convert(final Object value) {
                    if (value == null || value.equals("") || value.equals("N/A")) {
                        return null;
                    }
                    if (value instanceof Number) {
                        return ((Number) value).longValue();
                    }

                    try {
                        return Long.valueOf(value.toString());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse long number: " + value, e);
                    }

                    return null;
                }
            };

    private static final ValueConverter<Integer> INTEGER_CONVERTER =
            new ValueConverter<Integer>() {
                @Override
                public Integer convert(final Object value) {
                    if (value == null || value.equals("") || value.equals("N/A")) {
                        return null;
                    }
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    }

                    try {
                        return Integer.valueOf(value.toString());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse integer number: " + value, e);
                    }

                    return null;
                }
            };

    private static final ValueConverter<Float> FLOAT_CONVERTER =
            new ValueConverter<Float>() {
                @Override
                public Float convert(final Object value) {
                    if (value == null || value.equals("") || value.equals("N/A")) {
                        return null;
                    }
                    if (value instanceof Number) {
                        return ((Number) value).floatValue();
                    }

                    try {
                        return Float.valueOf(value.toString());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse float number: " + value, e);
                    }

                    return null;
                }
            };

    private static final ValueConverter<Double> DOUBLE_CONVERTER =
            new ValueConverter<Double>() {
                @Override
                public Double convert(final Object value) {
                    if (value == null || value.equals("") || value.equals("N/A")) {
                        return null;
                    }
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }

                    try {
                        return Double.valueOf(value.toString());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse float number: " + value, e);
                    }

                    return null;
                }
            };

    private static final ValueConverter<StreamType> STREAM_TYPE_CONVERTER =
            new ValueConverter<StreamType>() {
                @Override
                public StreamType convert(final Object value) {
                    if (value == null || value.equals("") || value.equals("N/A")) {
                        return null;
                    }

                    try {
                        return StreamType.valueOf(value.toString().toUpperCase());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse StreamType: " + value, e);
                    }

                    return null;
                }
            };

    private static final ValueConverter<Rational> RATIONAL_CONVERTER = new RationalConverter("/");

    private static final ValueConverter<Rational> RATIO_CONVERTER = new RationalConverter(":");

    private static class RationalConverter implements ValueConverter<Rational> {
        private final String delimiter;

        RationalConverter(final String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Rational convert(final Object value) {
            if (value == null || value.equals("") || value.equals("0/0") || value.equals("N/A")) {
                return null;
            }
            if (value instanceof Double) {
                return Rational.valueOf((Double) value);
            }
            if (value instanceof Float) {
                return Rational.valueOf((Float) value);
            }
            if (value instanceof Number) {
                return Rational.valueOf(((Number) value).longValue());
            }

            try {
                return Rational.valueOf(value.toString(), delimiter);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse rational number: " + value, e);
            }

            return null;
        }
    }
}
