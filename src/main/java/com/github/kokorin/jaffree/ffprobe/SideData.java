/*
 *    Copyright 2018-2021 Denis Kokorin
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
import com.github.kokorin.jaffree.ffprobe.data.ValueConverter;

/**
 * Side Data description.
 * <p>
 * Depending on side data type this structure may contain different set of properties.
 */
public class SideData {
    private final ProbeData probeData;

    /**
     * Creates {@link SideData}.
     *
     * @param probeData data section
     */
    public SideData(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for
     * current {@link SideData}.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * Returns side data type name.
     * <p>
     * Possible side data types for side data in {@link Packet} and {@link Stream} are represented by
     * <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavcodec/packet.h#L40">AVPacketSideDataType</a> enum.
     * <p>
     * Possible side data types for side data in {@link Frame} are represented by
     * <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/frame.h#L48">AVFrameSideDataType</a> enum.
     *
     * @return side data type name
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavcodec/packet.h#L370">av_packet_side_data_name</a>
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/frame.c#L826">av_frame_side_data_name</a>
     */
    public String getSideDataType() {
        return probeData.getString("side_data_type");
    }


    /**
     * May present only for &quot;3x3 displaymatrix&quot; ({@link Frame}) and
     * &quot;Display Matrix&quot; ({@link Packet} and {@link Stream}) side data types.
     *
     * @return display matrix
     */
    public String getDisplayMatrix() {
        return probeData.getString("displaymatrix");
    }

    /**
     * May present only for &quot;3x3 displaymatrix&quot; ({@link Frame}) and
     * &quot;Display Matrix&quot; ({@link Packet} and {@link Stream}) side data types.
     *
     * @return rotation in degrees
     */
    public Integer getRotation() {
        return probeData.getInteger("rotation");
    }

    /**
     * Shortcut for {@code this.getProbeData().getValue(name)}.
     *
     * @param name property name
     * @return property value
     */
    public Object getValue(String name) {
        return probeData.getValue(name);
    }

    /**
     * Shortcut for {@code this.getProbeData().getValue(name, converter)}.
     *
     * @param name      property name
     * @param converter converter to use
     * @param <T>       type to convert to
     * @return converted property value
     */
    public <T> T getValue(String name, ValueConverter<T> converter) {
        return probeData.getValue(name, converter);
    }

    /**
     * Shortcut for {@code this.getProbeData().getString(name)}.
     *
     * @param name property name
     * @return property value converted to {@link String}
     */
    public String getString(String name) {
        return probeData.getString(name);
    }


    /**
     * Shortcut for {@code this.getProbeData().getLong(name)}.
     *
     * @param name property name
     * @return property value converted to {@link Long}
     */
    public Long getLong(String name) {
        return probeData.getLong(name);
    }

    /**
     * Shortcut for {@code this.getProbeData().getDouble(name)}.
     *
     * @param name property name
     * @return property value converted to {@link Double}
     */
    public Double getDouble(String name) {
        return probeData.getDouble(name);
    }
}
