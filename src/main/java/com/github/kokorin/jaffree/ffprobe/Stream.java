
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.Section;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class Stream {
    private final Section section;

    public Stream(Section section) {
        this.section = section;
    }

    public StreamDisposition getDisposition() {
        return null;
    }

    public List<Tag> getTag() {
        return null;
    }

    public List<PacketSideData> getSideDataList() {
        //
        // @XmlElementWrapper(name = "side_data_list")
        // @XmlElement(name = "side_data")
        return null;
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
        return section.getRational("sample_aspect_ratio");
    }

    public Rational getDisplayAspectRatio() {
        return section.getRational("display_aspect_ratio");
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

    public Long getDurationTs() {
        return section.getLong("duration_ts");
    }

    public Float getDuration() {
        return section.getFloat("duration");
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


    public Long getStartTime(TimeUnit timeUnit) {
        return StreamExtension.getStartTime(this, timeUnit);
    }

    public Long getDuration(TimeUnit timeUnit) {
        return StreamExtension.getDuration(this, timeUnit);
    }

}
