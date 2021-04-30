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

import java.util.List;

/**
 * Program description.
 */
public class Stream implements TagAware {
    private final ProbeData probeData;

    /**
     * Creates {@link Stream} description based on provided ffprobe data.
     *
     * @param probeData ffprobe data
     */
    public Stream(final ProbeData probeData) {
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
     * Returns format-specific stream ID.
     *
     * @return stream ID
     */
    //TODO integer
    public String getId() {
        return probeData.getString("id");
    }

    /**
     * @return stream index
     */
    public Integer getIndex() {
        return probeData.getInteger("index");
    }

    /**
     * @return stream disposition
     */
    public StreamDisposition getDisposition() {
        return new StreamDisposition(probeData.getSubData("disposition"));
    }

    /**
     * Returns additional stream data that can be provided by the container.
     *
     * @return side data
     */
    public List<SideData> getSideDataList() {
        return probeData.getSubDataList("side_data_list", SideData::new);
    }

    /**
     * @return codec name
     */
    public String getCodecName() {
        return probeData.getString("codec_name");
    }

    /**
     * @return codec long name
     */
    public String getCodecLongName() {
        return probeData.getString("codec_long_name");
    }

    /**
     * @return codec profile
     */
    public String getProfile() {
        return probeData.getString("profile");
    }

    /**
     * Returns codec level. Video only.
     *
     * @return codec level
     */
    public Integer getLevel() {
        return probeData.getInteger("level");
    }

    /**
     * @return codec type
     */
    public StreamType getCodecType() {
        return probeData.getStreamType("codec_type");
    }

    /**
     * @return codec time base
     * @deprecated removed in ffmpeg source code
     */
    @Deprecated
    public Rational getCodecTimeBase() {
        return probeData.getRational("codec_time_base");
    }

    /**
     * Returns hexadecimal representation of fourcc codec tag, e.g. {@code 0x31637661}.
     *
     * @return codec tag
     * @see <a href="https://www.fourcc.org/">fourcc.org</a>
     */
    //TODO integer: hex number - FourCC
    public String getCodecTag() {
        return probeData.getString("codec_tag");
    }

    /**
     * Returns string representation of fourcc codec tag, e.g. {@code avc1}.
     *
     * @return codec tag
     * @see <a href="https://www.fourcc.org/">fourcc.org</a>
     */
    public String getCodecTagString() {
        return probeData.getString("codec_tag_string");
    }

    /**
     * Returns extra binary data needed for initializing the decoder, codec-dependent.
     *
     * @return extradata
     */
    public String getExtradata() {
        return probeData.getString("extradata");
    }

    /**
     * Returns extra binary data hash. Extradata is needed for initializing the decoder,
     * codec-dependent.
     *
     * @return extradata hash
     */
    public String getExtradataHash() {
        return probeData.getString("extradata_hash");
    }

    /**
     * Returns width of video or subtitle stream, or null.
     *
     * @return width
     * @see #getCodecType()
     */
    public Integer getWidth() {
        return probeData.getInteger("width");
    }

    /**
     * Returns height of video or subtitle stream, or null.
     *
     * @return height
     * @see #getCodecType()
     */
    public Integer getHeight() {
        return probeData.getInteger("height");
    }

    /**
     * Returns coded width of video stream, or null.
     *
     * @return coded width
     */
    public Integer getCodedWidth() {
        return probeData.getInteger("coded_width");
    }

    /**
     * Returns coded height of video stream, or null.
     *
     * @return coded height
     */
    public Integer getCodedHeight() {
        return probeData.getInteger("coded_height");
    }

    /**
     * Returns number of delayed frames of video stream, or null.
     *
     * @return number of delayed frames
     */
    public Integer hasBFrames() {
        return probeData.getInteger("has_b_frames");
    }

    /**
     * Returns sample aspect ratio (SAR) of video stream, or null.
     *
     * @return sar
     */
    public Rational getSampleAspectRatio() {
        return probeData.getRatio("sample_aspect_ratio");
    }

    /**
     * Returns display aspect ratio (DAR) of video stream, or null.
     *
     * @return dar
     */
    public Rational getDisplayAspectRatio() {
        return probeData.getRatio("display_aspect_ratio");
    }

    /**
     * Returns pixel format of video stream, or null.
     *
     * @return pixel format
     */
    public String getPixFmt() {
        return probeData.getString("pix_fmt");
    }

    /**
     * Returns visual content value range of video stream, or null.
     *
     * @return color range
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/pixfmt.h#L541">
     * enum AVColorRange</a>
     */
    public String getColorRange() {
        return probeData.getString("color_range");
    }

    /**
     * Returns YUV color space type of video stream, or null.
     *
     * @return color space
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/pixfmt.h#L502">
     * enum AVColorSpace</a>
     */
    public String getColorSpace() {
        return probeData.getString("color_space");
    }

    /**
     * Returns color transfer characteristic of video stream, or null.
     *
     * @return color transfer
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/pixfmt.h#L473">
     * enum AVColorTransferCharacteristic</a>
     */
    public String getColorTransfer() {
        return probeData.getString("color_transfer");
    }

    /**
     * Returns chromaticity coordinates of the source primaries of video stream, or null.
     *
     * @return color primaries
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/pixfmt.h#L448">
     * enum AVColorPrimaries</a>
     */
    public String getColorPrimaries() {
        return probeData.getString("color_primaries");
    }

    /**
     * Returns chroma location of video stream, or null.
     *
     * @return chroma location
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/pixfmt.h#L595">
     * enum AVChromaLocation</a>
     */
    public String getChromaLocation() {
        return probeData.getString("chroma_location");
    }

    /**
     * Returns field order of video stream, or null.
     *
     * @return field order
     * @see <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavcodec/codec_par.h#L36">
     * enum AVFieldOrder</a>
     */
    public String getFieldOrder() {
        return probeData.getString("field_order");
    }

    /**
     * Returns mpeg timecode string of video stream, or null.
     *
     * @return timecode
     * @deprecated removed in ffmpeg source code
     */
    @Deprecated
    public String getTimecode() {
        return probeData.getString("timecode");
    }

    /**
     * @return number of reference frames
     */
    public Integer getRefs() {
        return probeData.getInteger("refs");
    }

    /**
     * Returns sample format of audio stream, or null.
     *
     * @return sample format
     */
    public String getSampleFmt() {
        return probeData.getString("sample_fmt");
    }

    /**
     * Returns sample rate of audio stream, or null.
     *
     * @return sample rate
     */
    public Integer getSampleRate() {
        return probeData.getInteger("sample_rate");
    }

    /**
     * Returns number of channels in audio stream, or null.
     *
     * @return number of channels
     */
    public Integer getChannels() {
        return probeData.getInteger("channels");
    }

    /**
     * Returns channels layout of audio stream, or null.
     *
     * @return channels layout
     */
    public String getChannelLayout() {
        return probeData.getString("channel_layout");
    }

    /**
     * Returns number of bits per sample of audio stream, or null.
     *
     * @return bits per sample
     */
    public Integer getBitsPerSample() {
        return probeData.getInteger("bits_per_sample");
    }

    /**
     * Returns real base framerate of the stream.
     * <p>
     * This is the lowest framerate with which all timestamps can be represented accurately
     * (it is the least common multiple of all framerates in the stream).
     * <p>
     * Note, this value is just a guess! For example, if the time base is 1/90000 and all frames
     * have either approximately 3600 or 1800 timer ticks, then r_frame_rate will be 50/1.
     *
     * @return base framerate
     */
    public Rational getRFrameRate() {
        return probeData.getRational("r_frame_rate");
    }

    /**
     * @return average framerate
     */
    public Rational getAvgFrameRate() {
        return probeData.getRational("avg_frame_rate");
    }

    /**
     * Timebase is the fundamental unit of time (in seconds) in terms of which frame timestamps
     * are represented.
     *
     * @return stream timebase
     */
    // TODO Rational
    public String getTimeBase() {
        return probeData.getString("time_base");
    }

    /**
     * Returns presentation timestamp of the first frame of the stream in presentation order.
     *
     * @return start PTS
     */
    public Long getStartPts() {
        return probeData.getLong("start_pts");
    }

    /**
     * Returns time (in seconds) of the first frame of the stream in presentation order.
     *
     * @return start time
     */
    public Float getStartTime() {
        return probeData.getFloat("start_time");
    }

    /**
     * Stream duration in stream timebase units.
     *
     * @return duration in timebase units
     */
    public Long getDurationTs() {
        return probeData.getLong("duration_ts");
    }

    /**
     * @return duration in seconds
     */
    public Float getDuration() {
        return probeData.getFloat("duration");
    }

    /**
     * @return bit rate
     */
    public Integer getBitRate() {
        return probeData.getInteger("bit_rate");
    }

    /**
     * @return max bit rate
     */
    public Integer getMaxBitRate() {
        return probeData.getInteger("max_bit_rate");
    }

    /**
     * @return bits per raw sample
     */
    public Integer getBitsPerRawSample() {
        return probeData.getInteger("bits_per_raw_sample");
    }

    /**
     * @return number of frames
     */
    public Integer getNbFrames() {
        return probeData.getInteger("nb_frames");
    }


    /**
     * @return number of read frames
     */
    public Integer getNbReadFrames() {
        return probeData.getInteger("nb_read_frames");
    }


    /**
     * @return number of read packets
     */
    public Integer getNbReadPackets() {
        return probeData.getInteger("nb_read_packets");
    }
}
