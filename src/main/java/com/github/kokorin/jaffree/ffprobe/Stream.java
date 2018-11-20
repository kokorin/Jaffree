
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.Adapters.RatioAdapter;
import com.github.kokorin.jaffree.ffprobe.Adapters.RationalAdapter;
import com.github.kokorin.jaffree.ffprobe.Adapters.StreamTypeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * <p>Java class for streamType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="streamType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="disposition" type="{http://www.ffmpeg.org/schema/ffprobe}streamDispositionType" minOccurs="0"/&gt;
 *         &lt;element name="tag" type="{http://www.ffmpeg.org/schema/ffprobe}tagType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="side_data_list" type="{http://www.ffmpeg.org/schema/ffprobe}packetSideDataListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="codec_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="codec_long_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="codec_type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="codec_time_base" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="codec_tag" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="codec_tag_string" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="extradata" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="extradata_hash" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="coded_width" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="coded_height" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="has_b_frames" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="sample_aspect_ratio" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="display_aspect_ratio" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="pix_fmt" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="level" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="color_range" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color_space" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color_transfer" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color_primaries" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="chroma_location" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="field_order" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="timecode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="refs" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="sample_fmt" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sample_rate" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="channels" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="channel_layout" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="bits_per_sample" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="r_frame_rate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="avg_frame_rate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="time_base" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="start_pts" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="start_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="duration_ts" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="duration" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="bit_rate" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="max_bit_rate" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="bits_per_raw_sample" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="nb_frames" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="nb_read_frames" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="nb_read_packets" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "streamType", propOrder = {
    "disposition",
    "tag",
    "sideDataList"
})
public class Stream {

    protected StreamDisposition disposition;
    protected List<Tag> tag;
    @XmlElementWrapper(name = "side_data_list")
    @XmlElement(name = "side_data")
    protected List<PacketSideData> sideDataList;
    @XmlAttribute(name = "index", required = true)
    protected int index;
    @XmlAttribute(name = "codec_name")
    protected String codecName;
    @XmlAttribute(name = "codec_long_name")
    protected String codecLongName;
    @XmlAttribute(name = "profile")
    protected String profile;
    @XmlAttribute(name = "codec_type")
    @XmlJavaTypeAdapter(StreamTypeAdapter.class)
    protected StreamType codecType;
    @XmlAttribute(name = "codec_time_base", required = true)
    @XmlJavaTypeAdapter(RationalAdapter.class)
    protected com.github.kokorin.jaffree.Rational codecTimeBase;
    @XmlAttribute(name = "codec_tag", required = true)
    protected String codecTag;
    @XmlAttribute(name = "codec_tag_string", required = true)
    protected String codecTagString;
    @XmlAttribute(name = "extradata")
    protected String extradata;
    @XmlAttribute(name = "extradata_hash")
    protected String extradataHash;
    @XmlAttribute(name = "width")
    protected Integer width;
    @XmlAttribute(name = "height")
    protected Integer height;
    @XmlAttribute(name = "coded_width")
    protected Integer codedWidth;
    @XmlAttribute(name = "coded_height")
    protected Integer codedHeight;
    @XmlAttribute(name = "has_b_frames")
    protected Integer hasBFrames;
    @XmlAttribute(name = "sample_aspect_ratio")
    @XmlJavaTypeAdapter(RatioAdapter.class)
    protected com.github.kokorin.jaffree.Rational sampleAspectRatio;
    @XmlAttribute(name = "display_aspect_ratio")
    @XmlJavaTypeAdapter(RatioAdapter.class)
    protected com.github.kokorin.jaffree.Rational displayAspectRatio;
    @XmlAttribute(name = "pix_fmt")
    protected String pixFmt;
    @XmlAttribute(name = "level")
    protected Integer level;
    @XmlAttribute(name = "color_range")
    protected String colorRange;
    @XmlAttribute(name = "color_space")
    protected String colorSpace;
    @XmlAttribute(name = "color_transfer")
    protected String colorTransfer;
    @XmlAttribute(name = "color_primaries")
    protected String colorPrimaries;
    @XmlAttribute(name = "chroma_location")
    protected String chromaLocation;
    @XmlAttribute(name = "field_order")
    protected String fieldOrder;
    @XmlAttribute(name = "timecode")
    protected String timecode;
    @XmlAttribute(name = "refs")
    protected Integer refs;
    @XmlAttribute(name = "sample_fmt")
    protected String sampleFmt;
    @XmlAttribute(name = "sample_rate")
    protected Integer sampleRate;
    @XmlAttribute(name = "channels")
    protected Integer channels;
    @XmlAttribute(name = "channel_layout")
    protected String channelLayout;
    @XmlAttribute(name = "bits_per_sample")
    protected Integer bitsPerSample;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "r_frame_rate", required = true)
    @XmlJavaTypeAdapter(RationalAdapter.class)
    protected com.github.kokorin.jaffree.Rational rFrameRate;
    @XmlAttribute(name = "avg_frame_rate", required = true)
    @XmlJavaTypeAdapter(RationalAdapter.class)
    protected com.github.kokorin.jaffree.Rational avgFrameRate;
    @XmlAttribute(name = "time_base", required = true)
    protected String timeBase;
    @XmlAttribute(name = "start_pts")
    protected java.lang.Long startPts;
    @XmlAttribute(name = "start_time")
    protected Float startTime;
    @XmlAttribute(name = "duration_ts")
    protected java.lang.Long durationTs;
    @XmlAttribute(name = "duration")
    protected Float duration;
    @XmlAttribute(name = "bit_rate")
    protected Integer bitRate;
    @XmlAttribute(name = "max_bit_rate")
    protected Integer maxBitRate;
    @XmlAttribute(name = "bits_per_raw_sample")
    protected Integer bitsPerRawSample;
    @XmlAttribute(name = "nb_frames")
    protected Integer nbFrames;
    @XmlAttribute(name = "nb_read_frames")
    protected Integer nbReadFrames;
    @XmlAttribute(name = "nb_read_packets")
    protected Integer nbReadPackets;

    /**
     * Gets the value of the disposition property.
     * 
     * @return
     *     possible object is
     *     {@link StreamDisposition }
     *     
     */
    public StreamDisposition getDisposition() {
        return disposition;
    }

    /**
     * Sets the value of the disposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link StreamDisposition }
     *     
     */
    public void setDisposition(StreamDisposition value) {
        this.disposition = value;
    }

    /**
     * Gets the value of the tag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tag }
     * 
     * 
     */
    public List<Tag> getTag() {
        if (tag == null) {
            tag = new ArrayList<Tag>();
        }
        return this.tag;
    }

    /**
     * Gets the value of the index property.
     * 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     */
    public void setIndex(int value) {
        this.index = value;
    }

    /**
     * Gets the value of the codecName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodecName() {
        return codecName;
    }

    /**
     * Sets the value of the codecName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecName(String value) {
        this.codecName = value;
    }

    /**
     * Gets the value of the codecLongName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodecLongName() {
        return codecLongName;
    }

    /**
     * Sets the value of the codecLongName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecLongName(String value) {
        this.codecLongName = value;
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * Gets the value of the codecType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public StreamType getCodecType() {
        return codecType;
    }

    /**
     * Sets the value of the codecType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecType(StreamType value) {
        this.codecType = value;
    }

    /**
     * Gets the value of the codecTimeBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public com.github.kokorin.jaffree.Rational getCodecTimeBase() {
        return codecTimeBase;
    }

    /**
     * Sets the value of the codecTimeBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecTimeBase(com.github.kokorin.jaffree.Rational value) {
        this.codecTimeBase = value;
    }

    /**
     * Gets the value of the codecTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodecTag() {
        return codecTag;
    }

    /**
     * Sets the value of the codecTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecTag(String value) {
        this.codecTag = value;
    }

    /**
     * Gets the value of the codecTagString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodecTagString() {
        return codecTagString;
    }

    /**
     * Sets the value of the codecTagString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodecTagString(String value) {
        this.codecTagString = value;
    }

    /**
     * Gets the value of the extradata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtradata() {
        return extradata;
    }

    /**
     * Sets the value of the extradata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtradata(String value) {
        this.extradata = value;
    }

    /**
     * Gets the value of the extradataHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtradataHash() {
        return extradataHash;
    }

    /**
     * Sets the value of the extradataHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtradataHash(String value) {
        this.extradataHash = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWidth(Integer value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHeight(Integer value) {
        this.height = value;
    }

    /**
     * Gets the value of the codedWidth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodedWidth() {
        return codedWidth;
    }

    /**
     * Sets the value of the codedWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodedWidth(Integer value) {
        this.codedWidth = value;
    }

    /**
     * Gets the value of the codedHeight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodedHeight() {
        return codedHeight;
    }

    /**
     * Sets the value of the codedHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodedHeight(Integer value) {
        this.codedHeight = value;
    }

    /**
     * Gets the value of the hasBFrames property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHasBFrames() {
        return hasBFrames;
    }

    /**
     * Sets the value of the hasBFrames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHasBFrames(Integer value) {
        this.hasBFrames = value;
    }

    /**
     * Gets the value of the sampleAspectRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public com.github.kokorin.jaffree.Rational getSampleAspectRatio() {
        return sampleAspectRatio;
    }

    /**
     * Sets the value of the sampleAspectRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleAspectRatio(com.github.kokorin.jaffree.Rational value) {
        this.sampleAspectRatio = value;
    }

    /**
     * Gets the value of the displayAspectRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public com.github.kokorin.jaffree.Rational getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    /**
     * Sets the value of the displayAspectRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayAspectRatio(com.github.kokorin.jaffree.Rational value) {
        this.displayAspectRatio = value;
    }

    /**
     * Gets the value of the pixFmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPixFmt() {
        return pixFmt;
    }

    /**
     * Sets the value of the pixFmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPixFmt(String value) {
        this.pixFmt = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLevel(Integer value) {
        this.level = value;
    }

    /**
     * Gets the value of the colorRange property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorRange() {
        return colorRange;
    }

    /**
     * Sets the value of the colorRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorRange(String value) {
        this.colorRange = value;
    }

    /**
     * Gets the value of the colorSpace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorSpace() {
        return colorSpace;
    }

    /**
     * Sets the value of the colorSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorSpace(String value) {
        this.colorSpace = value;
    }

    /**
     * Gets the value of the colorTransfer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorTransfer() {
        return colorTransfer;
    }

    /**
     * Sets the value of the colorTransfer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorTransfer(String value) {
        this.colorTransfer = value;
    }

    /**
     * Gets the value of the colorPrimaries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorPrimaries() {
        return colorPrimaries;
    }

    /**
     * Sets the value of the colorPrimaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorPrimaries(String value) {
        this.colorPrimaries = value;
    }

    /**
     * Gets the value of the chromaLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChromaLocation() {
        return chromaLocation;
    }

    /**
     * Sets the value of the chromaLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChromaLocation(String value) {
        this.chromaLocation = value;
    }

    /**
     * Gets the value of the fieldOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldOrder() {
        return fieldOrder;
    }

    /**
     * Sets the value of the fieldOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldOrder(String value) {
        this.fieldOrder = value;
    }

    /**
     * Gets the value of the timecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimecode() {
        return timecode;
    }

    /**
     * Sets the value of the timecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimecode(String value) {
        this.timecode = value;
    }

    /**
     * Gets the value of the refs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRefs() {
        return refs;
    }

    /**
     * Sets the value of the refs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRefs(Integer value) {
        this.refs = value;
    }

    /**
     * Gets the value of the sampleFmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleFmt() {
        return sampleFmt;
    }

    /**
     * Sets the value of the sampleFmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleFmt(String value) {
        this.sampleFmt = value;
    }

    /**
     * Gets the value of the sampleRate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the value of the sampleRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSampleRate(Integer value) {
        this.sampleRate = value;
    }

    /**
     * Gets the value of the channels property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getChannels() {
        return channels;
    }

    /**
     * Sets the value of the channels property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChannels(Integer value) {
        this.channels = value;
    }

    /**
     * Gets the value of the channelLayout property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannelLayout() {
        return channelLayout;
    }

    /**
     * Sets the value of the channelLayout property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannelLayout(String value) {
        this.channelLayout = value;
    }

    /**
     * Gets the value of the bitsPerSample property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBitsPerSample() {
        return bitsPerSample;
    }

    /**
     * Sets the value of the bitsPerSample property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBitsPerSample(Integer value) {
        this.bitsPerSample = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the rFrameRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public com.github.kokorin.jaffree.Rational getRFrameRate() {
        return rFrameRate;
    }

    /**
     * Sets the value of the rFrameRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFrameRate(com.github.kokorin.jaffree.Rational value) {
        this.rFrameRate = value;
    }

    /**
     * Gets the value of the avgFrameRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public com.github.kokorin.jaffree.Rational getAvgFrameRate() {
        return avgFrameRate;
    }

    /**
     * Sets the value of the avgFrameRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvgFrameRate(com.github.kokorin.jaffree.Rational value) {
        this.avgFrameRate = value;
    }

    /**
     * Gets the value of the timeBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeBase() {
        return timeBase;
    }

    /**
     * Sets the value of the timeBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeBase(String value) {
        this.timeBase = value;
    }

    /**
     * Gets the value of the startPts property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Long }
     *     
     */
    public java.lang.Long getStartPts() {
        return startPts;
    }

    /**
     * Sets the value of the startPts property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Long }
     *     
     */
    public void setStartPts(java.lang.Long value) {
        this.startPts = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setStartTime(Float value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the durationTs property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Long }
     *     
     */
    public java.lang.Long getDurationTs() {
        return durationTs;
    }

    /**
     * Sets the value of the durationTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Long }
     *     
     */
    public void setDurationTs(java.lang.Long value) {
        this.durationTs = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDuration(Float value) {
        this.duration = value;
    }

    /**
     * Gets the value of the bitRate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBitRate() {
        return bitRate;
    }

    /**
     * Sets the value of the bitRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBitRate(Integer value) {
        this.bitRate = value;
    }

    /**
     * Gets the value of the maxBitRate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxBitRate() {
        return maxBitRate;
    }

    /**
     * Sets the value of the maxBitRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxBitRate(Integer value) {
        this.maxBitRate = value;
    }

    /**
     * Gets the value of the bitsPerRawSample property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBitsPerRawSample() {
        return bitsPerRawSample;
    }

    /**
     * Sets the value of the bitsPerRawSample property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBitsPerRawSample(Integer value) {
        this.bitsPerRawSample = value;
    }

    /**
     * Gets the value of the nbFrames property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNbFrames() {
        return nbFrames;
    }

    /**
     * Sets the value of the nbFrames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbFrames(Integer value) {
        this.nbFrames = value;
    }

    /**
     * Gets the value of the nbReadFrames property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNbReadFrames() {
        return nbReadFrames;
    }

    /**
     * Sets the value of the nbReadFrames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbReadFrames(Integer value) {
        this.nbReadFrames = value;
    }

    /**
     * Gets the value of the nbReadPackets property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNbReadPackets() {
        return nbReadPackets;
    }

    /**
     * Sets the value of the nbReadPackets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbReadPackets(Integer value) {
        this.nbReadPackets = value;
    }

    public List<PacketSideData> getSideDataList() {
        if (sideDataList == null) {
            sideDataList = new ArrayList<PacketSideData>();
        }
        return sideDataList;
    }

    public void setSideDataList(List<PacketSideData> sideDataList) {
        this.sideDataList = sideDataList;
    }

    public Long getStartTime(TimeUnit timeUnit) {
        return StreamExtension.getStartTime(this, timeUnit);
    }

    public Long getDuration(TimeUnit timeUnit) {
        return StreamExtension.getDuration(this, timeUnit);
    }

}
