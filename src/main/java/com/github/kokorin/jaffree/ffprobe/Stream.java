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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.DTag;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stream {
    private final DSection section;

    public Stream(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public StreamDisposition getDisposition() {
        return new StreamDisposition(section.getTag("DISPOSITION"));
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public List<PacketSideData> getSideDataList() {
        return section.getSections("SIDE_DATA", new DSection.SectionConverter<PacketSideData>() {
            @Override
            public PacketSideData convert(DSection dSection) {
                return new PacketSideData(dSection);
            }
        });
    }

    public int getIndex() {
        return section.getInteger("index");
    }

    public String getCodecName() {
        return section.getString("codec_name");
    }

    public String getCodecLongName() {
        return section.getString("codec_long_name");
    }

    public String getProfile() {
        return section.getString("profile");
    }

    public StreamType getCodecType() {
        return section.getStreamType("codec_type");
    }

    public Rational getCodecTimeBase() {
        return section.getRational("codec_time_base");
    }

    public String getCodecTag() {
        return section.getString("codec_tag");
    }

    public String getCodecTagString() {
        return section.getString("codec_tag_string");
    }

    public String getExtradata() {
        return section.getString("extradata");
    }

    public String getExtradataHash() {
        return section.getString("extradata_hash");
    }

    public Integer getWidth() {
        return section.getInteger("width");
    }

    public Integer getHeight() {
        return section.getInteger("height");
    }

    public Integer getCodedWidth() {
        return section.getInteger("coded_width");
    }

    public Integer getCodedHeight() {
        return section.getInteger("coded_height");
    }

    public Integer hasBFrames() {
        return section.getInteger("has_b_frames");
    }

    public Rational getSampleAspectRatio() {
        return section.getRatio("sample_aspect_ratio");
    }

    public Rational getDisplayAspectRatio() {
        return section.getRatio("display_aspect_ratio");
    }

    public String getPixFmt() {
        return section.getString("pix_fmt");
    }

    public Integer getLevel() {
        return section.getInteger("level");
    }

    public String getColorRange() {
        return section.getString("color_range");
    }

    public String getColorSpace() {
        return section.getString("color_space");
    }

    public String getColorTransfer() {
        return section.getString("color_transfer");
    }

    public String getColorPrimaries() {
        return section.getString("color_primaries");
    }

    public String getChromaLocation() {
        return section.getString("chroma_location");
    }

    public String getFieldOrder() {
        return section.getString("field_order");
    }

    public String getTimecode() {
        return section.getString("timecode");
    }

    public Integer getRefs() {
        return section.getInteger("refs");
    }

    public String getSampleFmt() {
        return section.getString("sample_fmt");
    }

    public Integer getSampleRate() {
        return section.getInteger("sample_rate");
    }

    public Integer getChannels() {
        return section.getInteger("channels");
    }

    public String getChannelLayout() {
        return section.getString("channel_layout");
    }

    public Integer getBitsPerSample() {
        return section.getInteger("bits_per_sample");
    }

    public String getId() {
        return section.getString("id");
    }

    public Rational getRFrameRate() {
        return section.getRational("r_frame_rate");
    }

    public Rational getAvgFrameRate() {
        return section.getRational("avg_frame_rate");
    }

    public String getTimeBase() {
        return section.getString("time_base");
    }

    public Long getStartPts() {
        return section.getLong("start_pts");
    }

    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    public Long getStartTime(TimeUnit timeUnit) {
        return fromSeconds(getStartTime(), timeUnit);
    }

    public Long getDurationTs() {
        return section.getLong("duration_ts");
    }

    public Float getDuration() {
        return section.getFloat("duration");
    }

    public Long getDuration(TimeUnit timeUnit) {
        return fromSeconds(getDuration(), timeUnit);
    }

    public Integer getBitRate() {
        return section.getInteger("bit_rate");
    }

    public Integer getMaxBitRate() {
        return section.getInteger("max_bit_rate");
    }

    public Integer getBitsPerRawSample() {
        return section.getInteger("bits_per_raw_sample");
    }

    public Integer getNbFrames() {
        return section.getInteger("nb_frames");
    }

    public Integer getNbReadFrames() {
        return section.getInteger("nb_read_frames");
    }

    public Integer getNbReadPackets() {
        return section.getInteger("nb_read_packets");
    }

    private static Long fromSeconds(Float seconds, TimeUnit timeUnit) {
        if (seconds == null) {
            return null;
        }

        long millis = (long) (1000 * seconds);
        return timeUnit.convert(millis, TimeUnit.MILLISECONDS);
    }
}
