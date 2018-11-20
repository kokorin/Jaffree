
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.Adapters.RatioAdapter;
import com.github.kokorin.jaffree.ffprobe.Adapters.StreamTypeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for frameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="frameType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tag" type="{http://www.ffmpeg.org/schema/ffprobe}tagType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="logs" type="{http://www.ffmpeg.org/schema/ffprobe}logsType" minOccurs="0"/&gt;
 *         &lt;element name="side_data_list" type="{http://www.ffmpeg.org/schema/ffprobe}frameSideDataListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="media_type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="stream_index" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="key_frame" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="pts" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pts_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="pkt_pts" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pkt_pts_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="pkt_dts" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pkt_dts_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="best_effort_timestamp" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="best_effort_timestamp_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="pkt_duration" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pkt_duration_time" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="pkt_pos" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pkt_size" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="sample_fmt" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="nb_samples" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="channels" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="channel_layout" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="pix_fmt" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sample_aspect_ratio" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="pict_type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="coded_picture_number" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="display_picture_number" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="interlaced_frame" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="top_field_first" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="repeat_pict" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frameType", propOrder = {
    "tag",
    "logs",
    "sideDataList"
})
public class Frame {

    protected List<Tag> tag;
    @XmlElementWrapper
    @XmlElement(name = "log")
    protected List<Log> logs;
    @XmlElementWrapper(name = "side_data_list")
    @XmlElement(name = "side_data")
    protected List<FrameSideData> sideDataList;
    @XmlAttribute(name = "media_type", required = true)
    @XmlJavaTypeAdapter(StreamTypeAdapter.class)
    protected StreamType mediaType;
    @XmlAttribute(name = "stream_index")
    protected Integer streamIndex;
    @XmlAttribute(name = "key_frame", required = true)
    protected int keyFrame;
    @XmlAttribute(name = "pts")
    protected Long pts;
    @XmlAttribute(name = "pts_time")
    protected Float ptsTime;
    @XmlAttribute(name = "pkt_pts")
    protected Long pktPts;
    @XmlAttribute(name = "pkt_pts_time")
    protected Float pktPtsTime;
    @XmlAttribute(name = "pkt_dts")
    protected Long pktDts;
    @XmlAttribute(name = "pkt_dts_time")
    protected Float pktDtsTime;
    @XmlAttribute(name = "best_effort_timestamp")
    protected Long bestEffortTimestamp;
    @XmlAttribute(name = "best_effort_timestamp_time")
    protected Float bestEffortTimestampTime;
    @XmlAttribute(name = "pkt_duration")
    protected Long pktDuration;
    @XmlAttribute(name = "pkt_duration_time")
    protected Float pktDurationTime;
    @XmlAttribute(name = "pkt_pos")
    protected Long pktPos;
    @XmlAttribute(name = "pkt_size")
    protected Integer pktSize;
    @XmlAttribute(name = "sample_fmt")
    protected String sampleFmt;
    @XmlAttribute(name = "nb_samples")
    protected Long nbSamples;
    @XmlAttribute(name = "channels")
    protected Integer channels;
    @XmlAttribute(name = "channel_layout")
    protected String channelLayout;
    @XmlAttribute(name = "width")
    protected Long width;
    @XmlAttribute(name = "height")
    protected Long height;
    @XmlAttribute(name = "pix_fmt")
    protected String pixFmt;
    @XmlAttribute(name = "sample_aspect_ratio")
    @XmlJavaTypeAdapter(RatioAdapter.class)
    protected Rational sampleAspectRatio;
    @XmlAttribute(name = "pict_type")
    protected String pictType;
    @XmlAttribute(name = "coded_picture_number")
    protected Long codedPictureNumber;
    @XmlAttribute(name = "display_picture_number")
    protected Long displayPictureNumber;
    @XmlAttribute(name = "interlaced_frame")
    protected Integer interlacedFrame;
    @XmlAttribute(name = "top_field_first")
    protected Integer topFieldFirst;
    @XmlAttribute(name = "repeat_pict")
    protected Integer repeatPict;

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
     * Gets the value of the mediaType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public StreamType getMediaType() {
        return mediaType;
    }

    /**
     * Sets the value of the mediaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediaType(StreamType value) {
        this.mediaType = value;
    }

    /**
     * Gets the value of the streamIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStreamIndex() {
        return streamIndex;
    }

    /**
     * Sets the value of the streamIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStreamIndex(Integer value) {
        this.streamIndex = value;
    }

    /**
     * Gets the value of the keyFrame property.
     * 
     */
    public int getKeyFrame() {
        return keyFrame;
    }

    /**
     * Sets the value of the keyFrame property.
     * 
     */
    public void setKeyFrame(int value) {
        this.keyFrame = value;
    }

    /**
     * Gets the value of the pts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPts() {
        return pts;
    }

    /**
     * Sets the value of the pts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPts(Long value) {
        this.pts = value;
    }

    /**
     * Gets the value of the ptsTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPtsTime() {
        return ptsTime;
    }

    /**
     * Sets the value of the ptsTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPtsTime(Float value) {
        this.ptsTime = value;
    }

    /**
     * Gets the value of the pktPts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPktPts() {
        return pktPts;
    }

    /**
     * Sets the value of the pktPts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPktPts(Long value) {
        this.pktPts = value;
    }

    /**
     * Gets the value of the pktPtsTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPktPtsTime() {
        return pktPtsTime;
    }

    /**
     * Sets the value of the pktPtsTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPktPtsTime(Float value) {
        this.pktPtsTime = value;
    }

    /**
     * Gets the value of the pktDts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPktDts() {
        return pktDts;
    }

    /**
     * Sets the value of the pktDts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPktDts(Long value) {
        this.pktDts = value;
    }

    /**
     * Gets the value of the pktDtsTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPktDtsTime() {
        return pktDtsTime;
    }

    /**
     * Sets the value of the pktDtsTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPktDtsTime(Float value) {
        this.pktDtsTime = value;
    }

    /**
     * Gets the value of the bestEffortTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBestEffortTimestamp() {
        return bestEffortTimestamp;
    }

    /**
     * Sets the value of the bestEffortTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBestEffortTimestamp(Long value) {
        this.bestEffortTimestamp = value;
    }

    /**
     * Gets the value of the bestEffortTimestampTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBestEffortTimestampTime() {
        return bestEffortTimestampTime;
    }

    /**
     * Sets the value of the bestEffortTimestampTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBestEffortTimestampTime(Float value) {
        this.bestEffortTimestampTime = value;
    }

    /**
     * Gets the value of the pktDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPktDuration() {
        return pktDuration;
    }

    /**
     * Sets the value of the pktDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPktDuration(Long value) {
        this.pktDuration = value;
    }

    /**
     * Gets the value of the pktDurationTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPktDurationTime() {
        return pktDurationTime;
    }

    /**
     * Sets the value of the pktDurationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPktDurationTime(Float value) {
        this.pktDurationTime = value;
    }

    /**
     * Gets the value of the pktPos property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPktPos() {
        return pktPos;
    }

    /**
     * Sets the value of the pktPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPktPos(Long value) {
        this.pktPos = value;
    }

    /**
     * Gets the value of the pktSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPktSize() {
        return pktSize;
    }

    /**
     * Sets the value of the pktSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPktSize(Integer value) {
        this.pktSize = value;
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
     * Gets the value of the nbSamples property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNbSamples() {
        return nbSamples;
    }

    /**
     * Sets the value of the nbSamples property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNbSamples(Long value) {
        this.nbSamples = value;
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
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWidth(Long value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHeight(Long value) {
        this.height = value;
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
     * Gets the value of the sampleAspectRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Rational getSampleAspectRatio() {
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
    public void setSampleAspectRatio(Rational value) {
        this.sampleAspectRatio = value;
    }

    /**
     * Gets the value of the pictType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPictType() {
        return pictType;
    }

    /**
     * Sets the value of the pictType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPictType(String value) {
        this.pictType = value;
    }

    /**
     * Gets the value of the codedPictureNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCodedPictureNumber() {
        return codedPictureNumber;
    }

    /**
     * Sets the value of the codedPictureNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCodedPictureNumber(Long value) {
        this.codedPictureNumber = value;
    }

    /**
     * Gets the value of the displayPictureNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDisplayPictureNumber() {
        return displayPictureNumber;
    }

    /**
     * Sets the value of the displayPictureNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDisplayPictureNumber(Long value) {
        this.displayPictureNumber = value;
    }

    /**
     * Gets the value of the interlacedFrame property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInterlacedFrame() {
        return interlacedFrame;
    }

    /**
     * Sets the value of the interlacedFrame property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInterlacedFrame(Integer value) {
        this.interlacedFrame = value;
    }

    /**
     * Gets the value of the topFieldFirst property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTopFieldFirst() {
        return topFieldFirst;
    }

    /**
     * Sets the value of the topFieldFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTopFieldFirst(Integer value) {
        this.topFieldFirst = value;
    }

    /**
     * Gets the value of the repeatPict property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRepeatPict() {
        return repeatPict;
    }

    /**
     * Sets the value of the repeatPict property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRepeatPict(Integer value) {
        this.repeatPict = value;
    }

    public List<Log> getLogs() {
        if (logs == null) {
            logs = new ArrayList<Log>();
        }
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public List<FrameSideData> getSideDataList() {
        if (sideDataList == null) {
            sideDataList = new ArrayList<FrameSideData>();
        }
        return sideDataList;
    }

    public void setSideDataList(List<FrameSideData> sideDataList) {
        this.sideDataList = sideDataList;
    }

}
