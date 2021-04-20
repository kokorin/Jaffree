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

import com.github.kokorin.jaffree.LogCategory;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;

import java.util.List;

/**
 * Interface describing tree-like data structure which holds parsed ffprobe output.
 */
public interface ProbeData {
    /**
     * Returns property value as is (without any conversion).
     *
     * @param name property name
     * @return property value
     */
    Object getValue(String name);

    /**
     * Returns property value converted to T-type using passed in convertor.
     *
     * @param name      property name
     * @param converter converter to use
     * @param <T>       result type
     * @return property value converted to T
     */
    <T> T getValue(String name, ValueConverter<T> converter);

    /**
     * Returns property value as {@link String} (using default converter).
     *
     * @param name property name
     * @return property value as {@link String}
     */
    String getString(String name);

    /**
     * Returns property value as {@link Boolean} (using default converter).
     *
     * @param name property name
     * @return property value as {@link Boolean}
     */
    Boolean getBoolean(String name);

    /**
     * Returns property value as {@link Long} (using default converter).
     *
     * @param name property name
     * @return property value as {@link Long}
     */
    Long getLong(String name);

    /**
     * Returns property value as {@link Integer} (using default converter).
     *
     * @param name property name
     * @return property value as {@link Integer}
     */
    Integer getInteger(String name);

    /**
     * Returns property value as {@link Float} (using default converter).
     *
     * @param name property name
     * @return property value as {@link Float}
     */
    Float getFloat(String name);

    /**
     * Returns property value as {@link Double} (using default converter).
     *
     * @param name property name
     * @return property value as {@link Double}
     */
    Double getDouble(String name);

    /**
     * Returns property value as {@link StreamType} (using default converter).
     *
     * @param name property name
     * @return property value as {@link StreamType}
     */
    StreamType getStreamType(String name);

    /**
     * Returns property value as {@link StreamType} (using default converter).
     *
     * @param name property name
     * @return property value as {@link StreamType}
     */
    LogLevel getLogLevel(String name);

    /**
     * Returns property value as {@link LogCategory} (using default converter).
     *
     * @param name property name
     * @return property value as {@link LogCategory}
     */
    LogCategory getLogCategory(String name);

    /**
     * Returns property value as {@link Rational} (using default converter).
     * <p>
     * Note: this method parses rationals with numerator and denominator separated with
     * / (slash) character
     *
     * @param name property name
     * @return property value as {@link Rational}
     * @see #getRatio(String)
     */
    Rational getRational(String name);

    /**
     * Returns property value as {@link Rational} (using default converter).
     * <p>
     * Note: this method parses rationals with numerator and denominator separated with
     * : (colon) character
     *
     * @param name property name
     * @return property value as {@link Rational}
     * @see #getRational(String)
     */
    Rational getRatio(String name);

    /**
     * Returns nested data list.
     *
     * @param name property name
     * @return nested data list
     */
    List<ProbeData> getSubDataList(String name);

    /**
     * Returns nested data list converted to {@link List} of T.
     *
     * @param name      property name
     * @param converter converter
     * @param <T>       result type
     * @return list of converted values
     */
    <T> List<T> getSubDataList(String name, ProbeDataConverter<T> converter);

    /**
     * Returns nested data.
     *
     * @param name sub-data name
     * @return nested property
     */
    ProbeData getSubData(String name);

    /**
     * Returns nested data converted to T type.
     *
     * @param name      sub-data name
     * @param converter converter
     * @param <T>       result type
     * @return nested property converted to T type
     */
    <T> T getSubData(String name, ProbeDataConverter<T> converter);

    /**
     * Returns value of nested data property.
     *
     * @param name     sub-data name
     * @param property property name
     * @return value of nested data property
     */
    Object getSubDataValue(String name, String property);

    /**
     * Returns value of nested data property converted to T type.
     *
     * @param name      sub-data name
     * @param property  property name
     * @param converter converter to use
     * @param <T>       result type
     * @return value of nested data property converted to T type
     */
    <T> T getSubDataValue(String name, String property, ValueConverter<T> converter);

    /**
     * Returns value of nested data property converted to {@link String}.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property {@link String} value
     */
    String getSubDataString(String subDataName, String property);

    /**
     * Returns value of nested data property converted to {@link Long}.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Long getSubDataLong(String subDataName, String property);

    /**
     * Returns value of nested data property converted to {@link Integer}.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Integer getSubDataInteger(String subDataName, String property);

    /**
     * Returns value of nested data property converted to {@link Double}.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Double getSubDataDouble(String subDataName, String property);

    /**
     * Returns value of nested data property converted to {@link Float}.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Float getSubDataFloat(String subDataName, String property);

}
