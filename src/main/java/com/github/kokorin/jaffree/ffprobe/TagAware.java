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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * {@link TagAware} interface provides common tag-related methods.
 */
public interface TagAware {

    /**
     * Returns data section which holds all the data provided by ffprobe.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters.
     *
     * @return probe data
     */
    ProbeData getProbeData();

    /**
     * Return tag string value by name.
     *
     * @param name tag name
     * @return tag value
     */
    default String getTag(String name) {
        return getProbeData().getSubDataString("tags", name);
    }

    /**
     * Return tag long value by name.
     *
     * @param name tag name
     * @return tag value
     */
    default Long getTagLong(String name) {
        return getProbeData().getSubDataLong("tags", name);
    }

    /**
     * Return tag integer value by name.
     *
     * @param name tag name
     * @return tag value
     */
    default Double getTagInteger(String name) {
        return getProbeData().getSubDataDouble("tags", name);
    }

    /**
     * Return tag double value by name.
     *
     * @param name tag name
     * @return tag value
     */
    default Double getTagDouble(String name) {
        return getProbeData().getSubDataDouble("tags", name);
    }

    /**
     * Return tag float value by name.
     *
     * @param name tag name
     * @return tag value
     */
    default Float getTagFloat(String name) {
        return getProbeData().getSubDataFloat("tags", name);
    }

}
