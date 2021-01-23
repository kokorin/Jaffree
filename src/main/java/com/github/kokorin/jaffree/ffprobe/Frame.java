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

/**
 * Frame description.
 *
 * @see FFprobe#setShowFrames(boolean)
 */
public class Frame {
    private final DSection section;

    /**
     * Creates {@link Frame} description based on provided data sections.
     *
     * @param section data section
     */
    public Frame(final DSection section) {
        this.section = section;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for
     * the current {@link Frame}.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public DSection getSection() {
        return section;
    }

    /**
     * Returns tags for this Frame.
     *
     * @return tags
     */
    // TODO Does Frame contain any tags?
    public List<Tag> getTags() {
        return section.getTag("TAG", "TAGS").getValues(DTag.TAG_CONVERTER);
    }

    // TODO Does Frame contain any tags?
    public String getTag(String name) {
        return section.getTag("TAG", "TAGS").getString(name);
    }

    /**
     * Returns logging information from the decoder about each frame according to the value
     * set in loglevel.
     *
     * @return logs
     * @see FFprobe#setShowLog(com.github.kokorin.jaffree.LogLevel)
     */
    public List<Log> getLogs() {
        return section.getSections("LOG", new DSection.SectionConverter<Log>() {
            @Override
            public Log convert(final DSection dSection) {
                return new Log(dSection);
            }
        });
    }

    /**
     * @return side data for the frame
     */
    public List<FrameSideData> getSideDataList() {
        return section.getSections("SIDE_DATA", new DSection.SectionConverter<FrameSideData>() {
            @Override
            public FrameSideData convert(final DSection dSection) {
                return new FrameSideData(dSection);
            }
        });
    }

    /**
     * @return media type
     */
    public StreamType getMediaType() {
        return section.getStreamType("media_type");
    }

    /**
     * @return corresponding stream id
     */
    public Integer getStreamIndex() {
        return section.getInteger("stream_index");
    }

    /**
     * @return 1 -> keyframe, 0-> not
     */
    // TODO make boolean
    public int getKeyFrame() {
        return section.getInteger("key_frame");
    }

    /**
     * Presentation timestamp in time_base units (time when frame should be shown to user).
     *
     * @return pts
     */
    public Long getPts() {
        return section.getLong("pts");
    }

    /**
     * Presentation timestamp in seconds (time when frame should be shown to user).
     *
     * @return pts in seconds
     */
    public Float getPtsTime() {
        return section.getFloat("pts_time");
    }

    /**
     * PTS in time_base units copied from the AVPacket that was decoded to produce this frame.
     *
     * @return packet pts
     * @deprecated use {@link #getPts()} instead (deprecated in ffmpeg)
     */
    public Long getPktPts() {
        return section.getLong("pkt_pts");
    }

    /**
     * PTS in seconds copied from the AVPacket that was decoded to produce this frame.
     *
     * @return packet pts
     * @deprecated use the {@link #getPtsTime()} instead (deprecated in ffmpeg)
     */
    public Float getPktPtsTime() {
        return section.getFloat("pkt_pts_time");
    }

    /**
     * DTS in time_base units copied from the AVPacket. (if frame threading isn't used)
     * This is also the Presentation time of this AVFrame calculated from
     * only AVPacket.dts values without pts values.
     *
     * @return packet DTS
     */
    public Long getPktDts() {
        return section.getLong("pkt_dts");
    }

    /**
     * DTS in seconds copied from the AVPacket. (if frame threading isn't used)
     * This is also the Presentation time of this AVFrame calculated from
     * only AVPacket.dts values without pts values.
     *
     * @return packet DTS time
     */
    public Float getPktDtsTime() {
        return section.getFloat("pkt_dts_time");
    }

    /**
     * Frame timestamp in stream time_base units, estimated using various heuristics.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: set by libavcodec, read by user</li>
     * </ul>
     *
     * @return best effort PTS
     */
    public Long getBestEffortTimestamp() {
        return section.getLong("best_effort_timestamp");
    }

    /**
     * Frame timestamp in seconds, estimated using various heuristics.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: set by libavcodec, read by user</li>
     * </ul>
     *
     * @return best effort time
     */
    public Float getBestEffortTimestampTime() {
        return section.getFloat("best_effort_timestamp_time");
    }

    /**
     * Duration of the corresponding packet in stream time_base units, 0 if unknown.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: read by user</li>
     * </ul>
     *
     * @return packet duration
     */
    public Long getPktDuration() {
        return section.getLong("pkt_duration");
    }

    /**
     * Duration of the corresponding packet in seconds, 0 if unknown.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: read by user</li>
     * </ul>
     *
     * @return packet duration
     */
    public Float getPktDurationTime() {
        return section.getFloat("pkt_duration_time");
    }

    /**
     * Reordered pos from the last AVPacket that has been input into the decoder.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: read by user</li>
     * </ul>
     *
     * @return packet position
     */
    public Long getPktPos() {
        return section.getLong("pkt_pos");
    }

    /**
     * Size of the corresponding packet containing the compressed frame.
     * <p>
     * It is set to a negative value if unknown.
     * <ul>
     * <li>encoding: unused</li>
     * <li>decoding: set by libavcodec, read by user</li>
     * </ul>
     *
     * @return packet size
     */
    public Integer getPktSize() {
        return section.getInteger("pkt_size");
    }

    /**
     * @return audio samples format
     */
    public String getSampleFmt() {
        return section.getString("sample_fmt");
    }

    /**
     * @return number of audio sample in a single channel
     */
    public Long getNbSamples() {
        return section.getLong("nb_samples");
    }

    /**
     * @return number of channels
     */
    public Integer getChannels() {
        return section.getInteger("channels");
    }

    /**
     * @return channels layout
     */
    public String getChannelLayout() {
        return section.getString("channel_layout");
    }

    /**
     * @return video frame width
     */
    public Long getWidth() {
        return section.getLong("width");
    }

    /**
     * @return video frame height
     */
    public Long getHeight() {
        return section.getLong("height");
    }

    /**
     * @return video frame pixel format
     */
    public String getPixFmt() {
        return section.getString("pix_fmt");
    }

    /**
     * Return sample aspect ratio for the video frame, 0/1 if unknown/unspecified.
     *
     * @return aspect ration
     */
    public Rational getSampleAspectRatio() {
        return section.getRatio("sample_aspect_ratio");
    }

    /**
     * Possible return values:
     * <ul>
     *     <li>I -> Intra</li>
     *     <li>P -> Predicted</li>
     *     <li>B -> Bi-dir predicted</li>
     *     <li>S -> S(GMC)-VOP MPEG-4</li>
     *     <li>i -> Switching Intra</li>
     *     <li>p -> Switching Predicted</li>
     *     <li>b -> BI type</li>
     *     <li>? - > unknown/undefined</li>
     * </ul>
     *
     * @return picture type of the frame
     */
    public String getPictType() {
        return section.getString("pict_type");
    }

    /**
     * @return picture number in bitstream order
     */
    public Long getCodedPictureNumber() {
        return section.getLong("coded_picture_number");
    }

    /**
     * @return picture number in display order
     */
    public Long getDisplayPictureNumber() {
        return section.getLong("display_picture_number");
    }

    /**
     * The content of the picture is interlaced.
     *
     * @return 1 -> interlaced, 0-> not
     */
    // TODO make boolean
    public Integer getInterlacedFrame() {
        return section.getInteger("interlaced_frame");
    }

    /**
     * If the content is interlaced, is top field displayed first.
     *
     * @return 1, if top field displayed first
     */
    // TODO make boolean
    public Integer getTopFieldFirst() {
        return section.getInteger("top_field_first");
    }

    /**
     * When decoding, this signals how much the picture must be delayed.
     * <p>
     * extra_delay = repeat_pict / (2*fps)
     *
     * @return picture extra delay
     */
    public Integer getRepeatPict() {
        return section.getInteger("repeat_pict");
    }
}
