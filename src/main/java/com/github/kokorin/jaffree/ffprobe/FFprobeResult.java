
package com.github.kokorin.jaffree.ffprobe;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ffprobeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ffprobeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="program_version" type="{http://www.ffmpeg.org/schema/ffprobe}programVersionType" minOccurs="0"/&gt;
 *         &lt;element name="library_versions" type="{http://www.ffmpeg.org/schema/ffprobe}libraryVersionsType" minOccurs="0"/&gt;
 *         &lt;element name="pixel_formats" type="{http://www.ffmpeg.org/schema/ffprobe}pixelFormatsType" minOccurs="0"/&gt;
 *         &lt;element name="packets" type="{http://www.ffmpeg.org/schema/ffprobe}packetsType" minOccurs="0"/&gt;
 *         &lt;element name="frames" type="{http://www.ffmpeg.org/schema/ffprobe}framesType" minOccurs="0"/&gt;
 *         &lt;element name="packets_and_frames" type="{http://www.ffmpeg.org/schema/ffprobe}packetsAndFramesType" minOccurs="0"/&gt;
 *         &lt;element name="programs" type="{http://www.ffmpeg.org/schema/ffprobe}programsType" minOccurs="0"/&gt;
 *         &lt;element name="streams" type="{http://www.ffmpeg.org/schema/ffprobe}streamsType" minOccurs="0"/&gt;
 *         &lt;element name="chapters" type="{http://www.ffmpeg.org/schema/ffprobe}chaptersType" minOccurs="0"/&gt;
 *         &lt;element name="format" type="{http://www.ffmpeg.org/schema/ffprobe}formatType" minOccurs="0"/&gt;
 *         &lt;element name="error" type="{http://www.ffmpeg.org/schema/ffprobe}errorType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
    @XmlElementWrapper(name = "library_versions")
    @XmlElement(name = "library_version")
    protected List<LibraryVersion> libraryVersions;
    @XmlElementWrapper(name = "pixel_formats")
    @XmlElement(name = "pixel_format")
    protected List<PixelFormat> pixelFormats;
    @XmlElementWrapper
    @XmlElement(name = "packet")
    protected List<Packet> packets;
    @XmlElementWrapper
    @XmlElements({
        @XmlElement(name = "frame", type = Frame.class),
        @XmlElement(name = "subtitle", type = Subtitle.class)
    })
    protected List<Object> frames;
    @XmlElementWrapper(name = "packets_and_frames")
    @XmlElements({
        @XmlElement(name = "packet", type = Packet.class),
        @XmlElement(name = "frame", type = Frame.class),
        @XmlElement(name = "subtitle", type = Subtitle.class)
    })
    protected List<Object> packetsAndFrames;
    @XmlElementWrapper
    @XmlElement(name = "program")
    protected List<Program> programs;
    @XmlElementWrapper
    @XmlElement(name = "stream")
    protected List<Stream> streams;
    @XmlElementWrapper
    @XmlElement(name = "chapter")
    protected List<Chapter> chapters;
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

    public List<LibraryVersion> getLibraryVersions() {
        if (libraryVersions == null) {
            libraryVersions = new ArrayList<LibraryVersion>();
        }
        return libraryVersions;
    }

    public void setLibraryVersions(List<LibraryVersion> libraryVersions) {
        this.libraryVersions = libraryVersions;
    }

    public List<PixelFormat> getPixelFormats() {
        if (pixelFormats == null) {
            pixelFormats = new ArrayList<PixelFormat>();
        }
        return pixelFormats;
    }

    public void setPixelFormats(List<PixelFormat> pixelFormats) {
        this.pixelFormats = pixelFormats;
    }

    public List<Packet> getPackets() {
        if (packets == null) {
            packets = new ArrayList<Packet>();
        }
        return packets;
    }

    public void setPackets(List<Packet> packets) {
        this.packets = packets;
    }

    public List<Object> getFrames() {
        if (frames == null) {
            frames = new ArrayList<Object>();
        }
        return frames;
    }

    public void setFrames(List<Object> frames) {
        this.frames = frames;
    }

    public List<Object> getPacketsAndFrames() {
        if (packetsAndFrames == null) {
            packetsAndFrames = new ArrayList<Object>();
        }
        return packetsAndFrames;
    }

    public void setPacketsAndFrames(List<Object> packetsAndFrames) {
        this.packetsAndFrames = packetsAndFrames;
    }

    public List<Program> getPrograms() {
        if (programs == null) {
            programs = new ArrayList<Program>();
        }
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public List<Stream> getStreams() {
        if (streams == null) {
            streams = new ArrayList<Stream>();
        }
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public List<Chapter> getChapters() {
        if (chapters == null) {
            chapters = new ArrayList<Chapter>();
        }
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

}
