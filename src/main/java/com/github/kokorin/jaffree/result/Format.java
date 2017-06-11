/*
 *    Copyright  2017 Denis Kokorin
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

package com.github.kokorin.jaffree.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for formatType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="formatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tag" type="{http://www.ffmpeg.org/schema/ffprobe}tagType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nb_streams" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="nb_programs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="format_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="format_long_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="start_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="duration" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="bit_rate" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="probe_score" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "formatType", propOrder = {
    "tag"
})
public class Format {

    protected List<Tag> tag;
    @XmlAttribute(name = "filename", required = true)
    protected String filename;
    @XmlAttribute(name = "nb_streams", required = true)
    protected int nbStreams;
    @XmlAttribute(name = "nb_programs", required = true)
    protected int nbPrograms;
    @XmlAttribute(name = "format_name", required = true)
    protected String formatName;
    @XmlAttribute(name = "format_long_name")
    protected String formatLongName;
    @XmlAttribute(name = "start_time")
    protected Float startTime;
    @XmlAttribute(name = "duration")
    protected Float duration;
    @XmlAttribute(name = "size")
    protected Long size;
    @XmlAttribute(name = "bit_rate")
    protected Long bitRate;
    @XmlAttribute(name = "probe_score")
    protected Integer probeScore;

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
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * Gets the value of the nbStreams property.
     * 
     */
    public int getNbStreams() {
        return nbStreams;
    }

    /**
     * Sets the value of the nbStreams property.
     * 
     */
    public void setNbStreams(int value) {
        this.nbStreams = value;
    }

    /**
     * Gets the value of the nbPrograms property.
     * 
     */
    public int getNbPrograms() {
        return nbPrograms;
    }

    /**
     * Sets the value of the nbPrograms property.
     * 
     */
    public void setNbPrograms(int value) {
        this.nbPrograms = value;
    }

    /**
     * Gets the value of the formatName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * Sets the value of the formatName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatName(String value) {
        this.formatName = value;
    }

    /**
     * Gets the value of the formatLongName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatLongName() {
        return formatLongName;
    }

    /**
     * Sets the value of the formatLongName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatLongName(String value) {
        this.formatLongName = value;
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
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSize(Long value) {
        this.size = value;
    }

    /**
     * Gets the value of the bitRate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBitRate() {
        return bitRate;
    }

    /**
     * Sets the value of the bitRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBitRate(Long value) {
        this.bitRate = value;
    }

    /**
     * Gets the value of the probeScore property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProbeScore() {
        return probeScore;
    }

    /**
     * Sets the value of the probeScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProbeScore(Integer value) {
        this.probeScore = value;
    }

}
