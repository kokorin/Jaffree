
package com.github.kokorin.jaffree.ffprobe.xml;

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
public class FFprobeType {

    @XmlElement(name = "program_version")
    protected ProgramVersionType programVersion;
    @XmlElement(name = "library_versions")
    protected LibraryVersionsType libraryVersions;
    @XmlElement(name = "pixel_formats")
    protected PixelFormatsType pixelFormats;
    protected PacketsType packets;
    protected FramesType frames;
    @XmlElement(name = "packets_and_frames")
    protected PacketsAndFramesType packetsAndFrames;
    protected ProgramsType programs;
    protected StreamsType streams;
    protected ChaptersType chapters;
    protected FormatType format;
    protected ErrorType error;

    /**
     * Gets the value of the programVersion property.
     * 
     * @return
     *     possible object is
     *     {@link ProgramVersionType }
     *     
     */
    public ProgramVersionType getProgramVersion() {
        return programVersion;
    }

    /**
     * Sets the value of the programVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgramVersionType }
     *     
     */
    public void setProgramVersion(ProgramVersionType value) {
        this.programVersion = value;
    }

    /**
     * Gets the value of the libraryVersions property.
     * 
     * @return
     *     possible object is
     *     {@link LibraryVersionsType }
     *     
     */
    public LibraryVersionsType getLibraryVersions() {
        return libraryVersions;
    }

    /**
     * Sets the value of the libraryVersions property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibraryVersionsType }
     *     
     */
    public void setLibraryVersions(LibraryVersionsType value) {
        this.libraryVersions = value;
    }

    /**
     * Gets the value of the pixelFormats property.
     * 
     * @return
     *     possible object is
     *     {@link PixelFormatsType }
     *     
     */
    public PixelFormatsType getPixelFormats() {
        return pixelFormats;
    }

    /**
     * Sets the value of the pixelFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link PixelFormatsType }
     *     
     */
    public void setPixelFormats(PixelFormatsType value) {
        this.pixelFormats = value;
    }

    /**
     * Gets the value of the packets property.
     * 
     * @return
     *     possible object is
     *     {@link PacketsType }
     *     
     */
    public PacketsType getPackets() {
        return packets;
    }

    /**
     * Sets the value of the packets property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketsType }
     *     
     */
    public void setPackets(PacketsType value) {
        this.packets = value;
    }

    /**
     * Gets the value of the frames property.
     * 
     * @return
     *     possible object is
     *     {@link FramesType }
     *     
     */
    public FramesType getFrames() {
        return frames;
    }

    /**
     * Sets the value of the frames property.
     * 
     * @param value
     *     allowed object is
     *     {@link FramesType }
     *     
     */
    public void setFrames(FramesType value) {
        this.frames = value;
    }

    /**
     * Gets the value of the packetsAndFrames property.
     * 
     * @return
     *     possible object is
     *     {@link PacketsAndFramesType }
     *     
     */
    public PacketsAndFramesType getPacketsAndFrames() {
        return packetsAndFrames;
    }

    /**
     * Sets the value of the packetsAndFrames property.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketsAndFramesType }
     *     
     */
    public void setPacketsAndFrames(PacketsAndFramesType value) {
        this.packetsAndFrames = value;
    }

    /**
     * Gets the value of the programs property.
     * 
     * @return
     *     possible object is
     *     {@link ProgramsType }
     *     
     */
    public ProgramsType getPrograms() {
        return programs;
    }

    /**
     * Sets the value of the programs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgramsType }
     *     
     */
    public void setPrograms(ProgramsType value) {
        this.programs = value;
    }

    /**
     * Gets the value of the streams property.
     * 
     * @return
     *     possible object is
     *     {@link StreamsType }
     *     
     */
    public StreamsType getStreams() {
        return streams;
    }

    /**
     * Sets the value of the streams property.
     * 
     * @param value
     *     allowed object is
     *     {@link StreamsType }
     *     
     */
    public void setStreams(StreamsType value) {
        this.streams = value;
    }

    /**
     * Gets the value of the chapters property.
     * 
     * @return
     *     possible object is
     *     {@link ChaptersType }
     *     
     */
    public ChaptersType getChapters() {
        return chapters;
    }

    /**
     * Sets the value of the chapters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChaptersType }
     *     
     */
    public void setChapters(ChaptersType value) {
        this.chapters = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link FormatType }
     *     
     */
    public FormatType getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormatType }
     *     
     */
    public void setFormat(FormatType value) {
        this.format = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorType }
     *     
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorType }
     *     
     */
    public void setError(ErrorType value) {
        this.error = value;
    }

}
