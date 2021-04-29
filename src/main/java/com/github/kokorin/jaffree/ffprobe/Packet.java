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

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

import java.util.List;

/**
 * Packet description.
 */
public class Packet implements TagAware, PacketFrameSubtitle {

    private final ProbeData probeData;

    /**
     * Creates {@link Packet} description based on provided ffprobe data.
     *
     * @param probeData ffprobe data
     */
    public Packet(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * Returns Packet presentation timestamp in {@link Stream} timebase units (the time at which
     * the decompressed packet will be presented to the user).
     *
     * @return pts
     * @see #getStreamIndex()
     * @see Stream#getTimebase()
     */
    public Long getPts() {
        return probeData.getLong("pts");
    }

    /**
     * Returns Packet presentation timestamp in seconds (time when frame should be shown to user).
     *
     * @return pts in seconds
     */
    public Float getPtsTime() {
        return probeData.getFloat("pts_time");
    }

    /**
     * Returns additional packet data that can be provided by the container.
     * Packet can contain several types of side information.
     *
     * @return side data for the packet
     */
    public List<SideData> getSideDataList() {
        return probeData.getSubDataList("side_data_list", SideData::new);
    }

    /**
     * Returns packet media type.
     *
     * @return media type
     */
    public StreamType getCodecType() {
        return probeData.getStreamType("codec_type");
    }

    /**
     * Returns packet stream index.
     *
     * @return stream index
     */
    public Integer getStreamIndex() {
        return probeData.getInteger("stream_index");
    }

    /**
     * Returns Packet decompression timestamp in {@link Stream} timebase units (the time at which
     * the packet is decompressed).
     *
     * @return dts
     * @see #getStreamIndex()
     * @see Stream#getTimebase()
     */
    public Long getDts() {
        return probeData.getLong("dts");
    }

    /**
     * Returns Packet decompression timestamp in seconds (the time at which the packet
     * is decompressed).
     *
     * @return pts in seconds
     */
    public Float getDtsTime() {
        return probeData.getFloat("dts_time");
    }

    /**
     * Returns duration of this packet in {@link Stream} timebase units, 0 if unknown.
     * Equals {@code next_pts - this_pts} in presentation order.
     *
     * @return packet duration
     */
    public Long getDuration() {
        return probeData.getLong("duration");
    }

    /**
     * Returns duration of this packet in seconds, 0 if unknown.
     *
     * @return duration in seconds
     */
    public Float getDurationTime() {
        return probeData.getFloat("duration_time");
    }

    /**
     * Same as the duration field, but as int64_t.
     *
     * @return packet duration
     * @see #getDuration()
     * @deprecated description from ffmpeg source code: this was required for Matroska subtitles,
     * whose duration values could overflow when the duration field was still an int.
     */
    public Long getConvergenceDuration() {
        return probeData.getLong("convergence_duration");
    }

    /**
     * Same as the duration_time field, but as int64_t.
     *
     * @return duration in seconds
     * @see #getDurationTime()
     * @deprecated description from ffmpeg source code: this was required for Matroska subtitles,
     * whose duration values could overflow when the duration field was still an int.
     */
    public Float getConvergenceDurationTime() {
        return probeData.getFloat("convergence_duration_time");
    }

    /**
     * Returns packet size.
     *
     * @return size
     */
    public Long getSize() {
        return probeData.getLong("size");
    }

    /**
     * Returns packet byte position in stream, -1 if unknown.
     *
     * @return position
     */
    public Long getPos() {
        return probeData.getLong("pos");
    }

    /**
     * Returns a combination of binary AV_PKT_FLAG values.
     *
     * @return binary flag
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavcodec/packet.h#L396">
     * AV_PKT_FLAG list</a>
     */
    public String getFlags() {
        return probeData.getString("flags");
    }

    /**
     * Returns packet data.
     *
     * @return packet data
     */
    public String getData() {
        return probeData.getString("data");
    }

    /**
     * Returns packet data hash.
     *
     * @return packet data hash
     */
    public String getDataHash() {
        return probeData.getString("data_hash");
    }
}
