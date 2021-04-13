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
    Object getValue(String name);

    <T> T getValue(String name, ValueConverter<T> converter);

    String getString(String key);

    Boolean getBoolean(String key);

    Long getLong(String key);

    Integer getInteger(String key);

    Float getFloat(String key);

    Double getDouble(String key);

    StreamType getStreamType(String key);

    LogLevel getLogLevel(String key);

    LogCategory getLogCategory(String key);

    Rational getRational(String key);

    Rational getRatio(String key);

    /**
     * @param name name
     * @return sections
     */
    List<ProbeData> getSubDataList(String name);

    /**
     * Handy method which returns property with specified name converted to {@link List} of T.
     *
     * @param name      property name
     * @param converter converter
     * @param <T>       result type
     * @return converted sections
     */
    <T> List<T> getSubDataList(String name, ProbeDataConverter<T> converter);

    /**
     * @param name name
     * @return sections
     */
    ProbeData getSubData(String name);

    /**
     * Handy method which returns property with specified name converted to T type.
     *
     * @param name      property name
     * @param converter converter
     * @param <T>       result type
     * @return converted sections
     */
    <T> T getSubData(String name, ProbeDataConverter<T> converter);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Object getSubDataValue(String subDataName, String property);

    /**
     * Handy method which returns subdata property with specified name converted to T type.
     *
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    <T> T getSubDataValue(String subDataName, String property, ValueConverter<T> converter);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    String getSubDataString(String subDataName, String property);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Long getSubDataLong(String subDataName, String property);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Integer getSubDataInteger(String subDataName, String property);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Double getSubDataDouble(String subDataName, String property);

    /**
     * @param subDataName sub-data name
     * @param property    property name
     * @return sub-data property value
     */
    Float getSubDataFloat(String subDataName, String property);

}
