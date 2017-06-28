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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ffprobeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ffprobeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="program_version" type="{http://www.ffmpeg.org/schema/ffprobe}programVersionType" minOccurs="0"/>
 *         &lt;element name="library_versions" type="{http://www.ffmpeg.org/schema/ffprobe}libraryVersionsType" minOccurs="0"/>
 *         &lt;element name="pixel_formats" type="{http://www.ffmpeg.org/schema/ffprobe}pixelFormatsType" minOccurs="0"/>
 *         &lt;element name="packets" type="{http://www.ffmpeg.org/schema/ffprobe}packetsType" minOccurs="0"/>
 *         &lt;element name="frames" type="{http://www.ffmpeg.org/schema/ffprobe}framesType" minOccurs="0"/>
 *         &lt;element name="packets_and_frames" type="{http://www.ffmpeg.org/schema/ffprobe}packetsAndFramesType" minOccurs="0"/>
 *         &lt;element name="programs" type="{http://www.ffmpeg.org/schema/ffprobe}programsType" minOccurs="0"/>
 *         &lt;element name="streams" type="{http://www.ffmpeg.org/schema/ffprobe}streamsType" minOccurs="0"/>
 *         &lt;element name="chapters" type="{http://www.ffmpeg.org/schema/ffprobe}chaptersType" minOccurs="0"/>
 *         &lt;element name="format" type="{http://www.ffmpeg.org/schema/ffprobe}formatType" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.ffmpeg.org/schema/ffprobe}errorType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ffprobeType", propOrder = {
    "programVersion",
    "libraryVersions",
    "pixelFormats",
    "packets",
    "frames",
    "packetsAndFrames",
    "programs",
    "streams",
    "chapters",
    "format",
    "error"
})
public class FFprobeResult {

    @XmlElement(name = "program_version")
    protected ProgramVersion programVersion;
    @XmlElement(name = "library_versions")
    protected LibraryVersions libraryVersions;
    @XmlElement(name = "pixel_formats")
    protected PixelFormats pixelFormats;
    protected Packets packets;
    protected Frames frames;
    @XmlElement(name = "packets_and_frames")
    protected PacketsAndFrames packetsAndFrames;
    protected Programs programs;
    protected Streams streams;
    protected Chapters chapters;
    protected Format format;
    protected Error error;

    /**
     * Gets the value of the programVersion property.
     * 
     * @return
     *     possible object is
     *     {@link ProgramVersion }
     *     
     */
    public ProgramVersion getProgramVersion() {
        return programVersion;
    }

    /**
     * Sets the value of the programVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgramVersion }
     *     
     */
    public void setProgramVersion(ProgramVersion value) {
        this.programVersion = value;
    }

    /**
     * Gets the value of the libraryVersions property.
     * 
     * @return
     *     possible object is
     *     {@link LibraryVersions }
     *     
     */
    public LibraryVersions getLibraryVersions() {
        return libraryVersions;
    }

    /**
     * Sets the value of the libraryVersions property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibraryVersions }
     *     
     */
    public void setLibraryVersions(LibraryVersions value) {
        this.libraryVersions = value;
    }

    /**
     * Gets the value of the pixelFormats property.
     * 
     * @return
     *     possible object is
     *     {@link PixelFormats }
     *     
     */
    public PixelFormats getPixelFormats() {
        return pixelFormats;
    }

    /**
     * Sets the value of the pixelFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link PixelFormats }
     *     
     */
    public void setPixelFormats(PixelFormats value) {
        this.pixelFormats = value;
    }

    /**
     * Gets the value of the packets property.
     * 
     * @return
     *     possible object is
     *     {@link Packets }
     *     
     */
    public Packets getPackets() {
        return packets;
    }

    /**
     * Sets the value of the packets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Packets }
     *     
     */
    public void setPackets(Packets value) {
        this.packets = value;
    }

    /**
     * Gets the value of the frames property.
     * 
     * @return
     *     possible object is
     *     {@link Frames }
     *     
     */
    public Frames getFrames() {
        return frames;
    }

    /**
     * Sets the value of the frames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Frames }
     *     
     */
    public void setFrames(Frames value) {
        this.frames = value;
    }

    /**
     * Gets the value of the packetsAndFrames property.
     * 
     * @return
     *     possible object is
     *     {@link PacketsAndFrames }
     *     
     */
    public PacketsAndFrames getPacketsAndFrames() {
        return packetsAndFrames;
    }

    /**
     * Sets the value of the packetsAndFrames property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketsAndFrames }
     *     
     */
    public void setPacketsAndFrames(PacketsAndFrames value) {
        this.packetsAndFrames = value;
    }

    /**
     * Gets the value of the programs property.
     * 
     * @return
     *     possible object is
     *     {@link Programs }
     *     
     */
    public Programs getPrograms() {
        return programs;
    }

    /**
     * Sets the value of the programs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Programs }
     *     
     */
    public void setPrograms(Programs value) {
        this.programs = value;
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
     * Gets the value of the chapters property.
     * 
     * @return
     *     possible object is
     *     {@link Chapters }
     *     
     */
    public Chapters getChapters() {
        return chapters;
    }

    /**
     * Sets the value of the chapters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Chapters }
     *     
     */
    public void setChapters(Chapters value) {
        this.chapters = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link Format }
     *     
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link Format }
     *     
     */
    public void setFormat(Format value) {
        this.format = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link Error }
     *     
     */
    public Error getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link Error }
     *     
     */
    public void setError(Error value) {
        this.error = value;
    }

}
