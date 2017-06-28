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

package com.github.kokorin.jaffree.ffprobe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for programType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="programType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tag" type="{http://www.ffmpeg.org/schema/ffprobe}tagType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="streams" type="{http://www.ffmpeg.org/schema/ffprobe}streamsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="program_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="program_num" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="nb_streams" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="start_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="start_pts" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="end_time" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="end_pts" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="pmt_pid" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pcr_pid" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "programType", propOrder = {
    "tag",
    "streams"
})
public class Program {

    protected List<Tag> tag;
    protected Streams streams;
    @XmlAttribute(name = "program_id", required = true)
    protected int programId;
    @XmlAttribute(name = "program_num", required = true)
    protected int programNum;
    @XmlAttribute(name = "nb_streams", required = true)
    protected int nbStreams;
    @XmlAttribute(name = "start_time")
    protected Float startTime;
    @XmlAttribute(name = "start_pts")
    protected Long startPts;
    @XmlAttribute(name = "end_time")
    protected Float endTime;
    @XmlAttribute(name = "end_pts")
    protected Long endPts;
    @XmlAttribute(name = "pmt_pid", required = true)
    protected int pmtPid;
    @XmlAttribute(name = "pcr_pid", required = true)
    protected int pcrPid;

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
     * Gets the value of the streams property.
     * 
     * @return
     *     possible object is
     *     {@link Streams }
     *     
     */
    public Streams getStreams() {
        return streams;
    }

    /**
     * Sets the value of the streams property.
     * 
     * @param value
     *     allowed object is
     *     {@link Streams }
     *     
     */
    public void setStreams(Streams value) {
        this.streams = value;
    }

    /**
     * Gets the value of the programId property.
     * 
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Sets the value of the programId property.
     * 
     */
    public void setProgramId(int value) {
        this.programId = value;
    }

    /**
     * Gets the value of the programNum property.
     * 
     */
    public int getProgramNum() {
        return programNum;
    }

    /**
     * Sets the value of the programNum property.
     * 
     */
    public void setProgramNum(int value) {
        this.programNum = value;
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
     * Gets the value of the startPts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStartPts() {
        return startPts;
    }

    /**
     * Sets the value of the startPts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStartPts(Long value) {
        this.startPts = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setEndTime(Float value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the endPts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEndPts() {
        return endPts;
    }

    /**
     * Sets the value of the endPts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEndPts(Long value) {
        this.endPts = value;
    }

    /**
     * Gets the value of the pmtPid property.
     * 
     */
    public int getPmtPid() {
        return pmtPid;
    }

    /**
     * Sets the value of the pmtPid property.
     * 
     */
    public void setPmtPid(int value) {
        this.pmtPid = value;
    }

    /**
     * Gets the value of the pcrPid property.
     * 
     */
    public int getPcrPid() {
        return pcrPid;
    }

    /**
     * Sets the value of the pcrPid property.
     * 
     */
    public void setPcrPid(int value) {
        this.pcrPid = value;
    }

}
