
package com.github.kokorin.jaffree.ffprobe.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for packetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tag" type="{http://www.ffmpeg.org/schema/ffprobe}tagType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="side_data_list" type="{http://www.ffmpeg.org/schema/ffprobe}packetSideDataListType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="codec_type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stream_index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pts" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="pts_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="dts" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="dts_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="duration" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="duration_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="convergence_duration" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="convergence_duration_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="size" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="pos" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="flags" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="data" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="data_hash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "packetType", propOrder = {
    "tag",
    "sideDataList"
})
public class PacketType {

    protected List<TagType> tag;
    @XmlElement(name = "side_data_list")
    protected PacketSideDataListType sideDataList;
    @XmlAttribute(name = "codec_type", required = true)
    protected String codecType;
    @XmlAttribute(name = "stream_index", required = true)
    protected int streamIndex;
    @XmlAttribute(name = "pts")
    protected Long pts;
    @XmlAttribute(name = "pts_time")
    protected Float ptsTime;
    @XmlAttribute(name = "dts")
    protected Long dts;
    @XmlAttribute(name = "dts_time")
    protected Float dtsTime;
    @XmlAttribute(name = "duration")
    protected Long duration;
    @XmlAttribute(name = "duration_time")
    protected Float durationTime;
    @XmlAttribute(name = "convergence_duration")
    protected Long convergenceDuration;
    @XmlAttribute(name = "convergence_duration_time")
    protected Float convergenceDurationTime;
    @XmlAttribute(name = "size", required = true)
    protected long size;
    @XmlAttribute(name = "pos")
    protected Long pos;
    @XmlAttribute(name = "flags", required = true)
    protected String flags;
    @XmlAttribute(name = "data")
    protected String data;
    @XmlAttribute(name = "data_hash")
    protected String dataHash;

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
     * {@link TagType }
     * 
     * 
     */
    public List<TagType> getTag() {
        if (tag == null) {
            tag = new ArrayList<TagType>();
        }
        return this.tag;
    }

    /**
     * Gets the value of the sideDataList property.
     * 
     * @return
     *     possible object is
     *     {@link PacketSideDataListType }
     *     
     */
    public PacketSideDataListType getSideDataList() {
        return sideDataList;
    }

    /**
     * Sets the value of the sideDataList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketSideDataListType }
     *     
     */
    public void setSideDataList(PacketSideDataListType value) {
        this.sideDataList = value;
    }

    /**
     * Gets the value of the codecType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodecType() {
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
    public void setCodecType(String value) {
        this.codecType = value;
    }

    /**
     * Gets the value of the streamIndex property.
     * 
     */
    public int getStreamIndex() {
        return streamIndex;
    }

    /**
     * Sets the value of the streamIndex property.
     * 
     */
    public void setStreamIndex(int value) {
        this.streamIndex = value;
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
     * Gets the value of the dts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDts() {
        return dts;
    }

    /**
     * Sets the value of the dts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDts(Long value) {
        this.dts = value;
    }

    /**
     * Gets the value of the dtsTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDtsTime() {
        return dtsTime;
    }

    /**
     * Sets the value of the dtsTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDtsTime(Float value) {
        this.dtsTime = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDuration(Long value) {
        this.duration = value;
    }

    /**
     * Gets the value of the durationTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDurationTime() {
        return durationTime;
    }

    /**
     * Sets the value of the durationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDurationTime(Float value) {
        this.durationTime = value;
    }

    /**
     * Gets the value of the convergenceDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getConvergenceDuration() {
        return convergenceDuration;
    }

    /**
     * Sets the value of the convergenceDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setConvergenceDuration(Long value) {
        this.convergenceDuration = value;
    }

    /**
     * Gets the value of the convergenceDurationTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getConvergenceDurationTime() {
        return convergenceDurationTime;
    }

    /**
     * Sets the value of the convergenceDurationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setConvergenceDurationTime(Float value) {
        this.convergenceDurationTime = value;
    }

    /**
     * Gets the value of the size property.
     * 
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     */
    public void setSize(long value) {
        this.size = value;
    }

    /**
     * Gets the value of the pos property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPos() {
        return pos;
    }

    /**
     * Sets the value of the pos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPos(Long value) {
        this.pos = value;
    }

    /**
     * Gets the value of the flags property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlags() {
        return flags;
    }

    /**
     * Sets the value of the flags property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlags(String value) {
        this.flags = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    /**
     * Gets the value of the dataHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataHash() {
        return dataHash;
    }

    /**
     * Sets the value of the dataHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataHash(String value) {
        this.dataHash = value;
    }

}
