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

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;
import com.github.kokorin.jaffree.ffprobe.data.ProbeDataConverter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stream implements TagAware {
    private final ProbeData probeData;

    public Stream(ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProbeData getProbeData() {
        return probeData;
    }

    public StreamDisposition getDisposition() {
        return new StreamDisposition(probeData.getSubData("disposition"));
    }

    public List<SideData> getSideDataList() {
        return probeData.getSubDataList("side_data_list", new ProbeDataConverter<SideData>() {
            @Override
            public SideData convert(ProbeData dSection) {
                return new SideData(dSection);
            }
        });
    }

    public Integer getIndex() {
        return probeData.getInteger("index");
    }

    public String getCodecName() {
        return probeData.getString("codec_name");
    }

    public String getCodecLongName() {
        return probeData.getString("codec_long_name");
    }

    public String getProfile() {
        return probeData.getString("profile");
    }

    public StreamType getCodecType() {
        return probeData.getStreamType("codec_type");
    }

    public Rational getCodecTimeBase() {
        return probeData.getRational("codec_time_base");
    }

    public String getCodecTag() {
        return probeData.getString("codec_tag");
    }

    public String getCodecTagString() {
        return probeData.getString("codec_tag_string");
    }

    public String getExtradata() {
        return probeData.getString("extradata");
    }

    public String getExtradataHash() {
        return probeData.getString("extradata_hash");
    }

    public Integer getWidth() {
        return probeData.getInteger("width");
    }

    public Integer getHeight() {
        return probeData.getInteger("height");
    }

    public Integer getCodedWidth() {
        return probeData.getInteger("coded_width");
    }

    public Integer getCodedHeight() {
        return probeData.getInteger("coded_height");
    }

    public Integer hasBFrames() {
        return probeData.getInteger("has_b_frames");
    }

    public Rational getSampleAspectRatio() {
        return probeData.getRatio("sample_aspect_ratio");
    }

    public Rational getDisplayAspectRatio() {
        return probeData.getRatio("display_aspect_ratio");
    }

    public String getPixFmt() {
        return probeData.getString("pix_fmt");
    }

    public Integer getLevel() {
        return probeData.getInteger("level");
    }

    public String getColorRange() {
        return probeData.getString("color_range");
    }

    public String getColorSpace() {
        return probeData.getString("color_space");
    }

    public String getColorTransfer() {
        return probeData.getString("color_transfer");
    }

    public String getColorPrimaries() {
        return probeData.getString("color_primaries");
    }

    public String getChromaLocation() {
        return probeData.getString("chroma_location");
    }

    public String getFieldOrder() {
        return probeData.getString("field_order");
    }

    public String getTimecode() {
        return probeData.getString("timecode");
    }

    public Integer getRefs() {
        return probeData.getInteger("refs");
    }

    public String getSampleFmt() {
        return probeData.getString("sample_fmt");
    }

    public Integer getSampleRate() {
        return probeData.getInteger("sample_rate");
    }

    public Integer getChannels() {
        return probeData.getInteger("channels");
    }

    public String getChannelLayout() {
        return probeData.getString("channel_layout");
    }

    public Long getBitsPerSample() {
        return probeData.getLong("bits_per_sample");
    }

    public String getId() {
        return probeData.getString("id");
    }

    public Rational getRFrameRate() {
        return probeData.getRational("r_frame_rate");
    }

    public Rational getAvgFrameRate() {
        return probeData.getRational("avg_frame_rate");
    }

    public String getTimeBase() {
        return probeData.getString("time_base");
    }

    public Long getStartPts() {
        return probeData.getLong("start_pts");
    }

    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    public Long getStartTime(TimeUnit timeUnit) {
        return fromSeconds(getStartTime(), timeUnit);
    }

    public Long getDurationTs() {
        return probeData.getLong("duration_ts");
    }

    public Float getDuration() {
        return probeData.getFloat("duration");
    }

    public Long getDuration(TimeUnit timeUnit) {
        return fromSeconds(getDuration(), timeUnit);
    }

    public Long getBitRate() {
        return probeData.getLong("bit_rate");
    }

    public Long getMaxBitRate() {
        return probeData.getLong("max_bit_rate");
    }

    public Long getBitsPerRawSample() {
        return probeData.getLong("bits_per_raw_sample");
    }

    public Integer getNbFrames() {
        return probeData.getInteger("nb_frames");
    }

    public Integer getNbReadFrames() {
        return probeData.getInteger("nb_read_frames");
    }

    public Integer getNbReadPackets() {
        return probeData.getInteger("nb_read_packets");
    }

    private static Long fromSeconds(Float seconds, TimeUnit timeUnit) {
        if (seconds == null) {
            return null;
        }

        long millis = (long) (1000 * seconds);
        return timeUnit.convert(millis, TimeUnit.MILLISECONDS);
    }
}
