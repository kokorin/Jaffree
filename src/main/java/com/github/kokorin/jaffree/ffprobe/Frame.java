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

public class Frame {
    private final DSection section;

    public Frame(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public List<Log> getLogs() {
        return section.getSections("LOG", new DSection.SectionConverter<Log>() {
            @Override
            public Log convert(DSection dSection) {
                return new Log(dSection);
            }
        });
    }

    public List<FrameSideData> getSideDataList() {
        return section.getSections("SIDE_DATA", new DSection.SectionConverter<FrameSideData>() {
            @Override
            public FrameSideData convert(DSection dSection) {
               return new FrameSideData(dSection);
            }
        });
    }

    public StreamType getMediaType() {
        return section.getStreamType("media_type");
    }

    public Integer getStreamIndex() {
        return section.getInteger("stream_index");
    }

    public int getKeyFrame() {
        return section.getInteger("key_frame");
    }

    public Long getPts() {
        return section.getLong("pts");
    }

    public Float getPtsTime() {
        return section.getFloat("pts_time");
    }

    public Long getPktPts() {
        return section.getLong("pkt_pts");
    }

    public Float getPktPtsTime() {
        return section.getFloat("pkt_pts_time");
    }

    public Long getPktDts() {
        return section.getLong("pkt_dts");
    }

    public Float getPktDtsTime() {
        return section.getFloat("pkt_dts_time");
    }

    public Long getBestEffortTimestamp() {
        return section.getLong("best_effort_timestamp");
    }

    public Float getBestEffortTimestampTime() {
        return section.getFloat("best_effort_timestamp_time");
    }

    public Long getPktDuration() {
        return section.getLong("pkt_duration");
    }

    public Float getPktDurationTime() {
        return section.getFloat("pkt_duration_time");
    }

    public Long getPktPos() {
        return section.getLong("pkt_pos");
    }

    public Integer getPktSize() {
        return section.getInteger("pkt_size");
    }

    public String getSampleFmt() {
        return section.getString("sample_fmt");
    }

    public Long getNbSamples() {
        return section.getLong("nb_samples");
    }

    public Integer getChannels() {
        return section.getInteger("channels");
    }

    public String getChannelLayout() {
        return section.getString("channel_layout");
    }

    public Long getWidth() {
        return section.getLong("width");
    }

    public Long getHeight() {
        return section.getLong("height");
    }

    public String getPixFmt() {
        return section.getString("pix_fmt");
    }

    public Rational getSampleAspectRatio() {
        return section.getRatio("sample_aspect_ratio");
    }

    public String getPictType() {
        return section.getString("pict_type");
    }

    public Long getCodedPictureNumber() {
        return section.getLong("coded_picture_number");
    }

    public Long getDisplayPictureNumber() {
        return section.getLong("display_picture_number");
    }

    public Integer getInterlacedFrame() {
        return section.getInteger("interlaced_frame");
    }

    public Integer getTopFieldFirst() {
        return section.getInteger("top_field_first");
    }

    public Integer getRepeatPict() {
        return section.getInteger("repeat_pict");
    }
}
