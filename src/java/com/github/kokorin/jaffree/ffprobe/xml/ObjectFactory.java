
package com.github.kokorin.jaffree.ffprobe.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.github.kokorin.jaffree.ffprobe.xml package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Ffprobe_QNAME = new QName("http://www.ffmpeg.org/schema/ffprobe", "ffprobe");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.github.kokorin.jaffree.ffprobe.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FFprobeType }
     * 
     */
    public FFprobeType createFfprobeType() {
        return new FFprobeType();
    }

    /**
     * Create an instance of {@link LogType }
     * 
     */
    public LogType createLogType() {
        return new LogType();
    }

    /**
     * Create an instance of {@link StreamsType }
     * 
     */
    public StreamsType createStreamsType() {
        return new StreamsType();
    }

    /**
     * Create an instance of {@link PacketsAndFramesType }
     * 
     */
    public PacketsAndFramesType createPacketsAndFramesType() {
        return new PacketsAndFramesType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link ProgramVersionType }
     * 
     */
    public ProgramVersionType createProgramVersionType() {
        return new ProgramVersionType();
    }

    /**
     * Create an instance of {@link PixelFormatComponentsType }
     * 
     */
    public PixelFormatComponentsType createPixelFormatComponentsType() {
        return new PixelFormatComponentsType();
    }

    /**
     * Create an instance of {@link SubtitleType }
     * 
     */
    public SubtitleType createSubtitleType() {
        return new SubtitleType();
    }

    /**
     * Create an instance of {@link LibraryVersionsType }
     * 
     */
    public LibraryVersionsType createLibraryVersionsType() {
        return new LibraryVersionsType();
    }

    /**
     * Create an instance of {@link StreamType }
     * 
     */
    public StreamType createStreamType() {
        return new StreamType();
    }

    /**
     * Create an instance of {@link TagType }
     * 
     */
    public TagType createTagType() {
        return new TagType();
    }

    /**
     * Create an instance of {@link LogsType }
     * 
     */
    public LogsType createLogsType() {
        return new LogsType();
    }

    /**
     * Create an instance of {@link FormatType }
     * 
     */
    public FormatType createFormatType() {
        return new FormatType();
    }

    /**
     * Create an instance of {@link PixelFormatComponentType }
     * 
     */
    public PixelFormatComponentType createPixelFormatComponentType() {
        return new PixelFormatComponentType();
    }

    /**
     * Create an instance of {@link PixelFormatsType }
     * 
     */
    public PixelFormatsType createPixelFormatsType() {
        return new PixelFormatsType();
    }

    /**
     * Create an instance of {@link PacketType }
     * 
     */
    public PacketType createPacketType() {
        return new PacketType();
    }

    /**
     * Create an instance of {@link PacketSideDataType }
     * 
     */
    public PacketSideDataType createPacketSideDataType() {
        return new PacketSideDataType();
    }

    /**
     * Create an instance of {@link ProgramType }
     * 
     */
    public ProgramType createProgramType() {
        return new ProgramType();
    }

    /**
     * Create an instance of {@link ChaptersType }
     * 
     */
    public ChaptersType createChaptersType() {
        return new ChaptersType();
    }

    /**
     * Create an instance of {@link PixelFormatFlagsType }
     * 
     */
    public PixelFormatFlagsType createPixelFormatFlagsType() {
        return new PixelFormatFlagsType();
    }

    /**
     * Create an instance of {@link StreamDispositionType }
     * 
     */
    public StreamDispositionType createStreamDispositionType() {
        return new StreamDispositionType();
    }

    /**
     * Create an instance of {@link PacketSideDataListType }
     * 
     */
    public PacketSideDataListType createPacketSideDataListType() {
        return new PacketSideDataListType();
    }

    /**
     * Create an instance of {@link ProgramsType }
     * 
     */
    public ProgramsType createProgramsType() {
        return new ProgramsType();
    }

    /**
     * Create an instance of {@link PixelFormatType }
     * 
     */
    public PixelFormatType createPixelFormatType() {
        return new PixelFormatType();
    }

    /**
     * Create an instance of {@link FrameSideDataListType }
     * 
     */
    public FrameSideDataListType createFrameSideDataListType() {
        return new FrameSideDataListType();
    }

    /**
     * Create an instance of {@link FramesType }
     * 
     */
    public FramesType createFramesType() {
        return new FramesType();
    }

    /**
     * Create an instance of {@link LibraryVersionType }
     * 
     */
    public LibraryVersionType createLibraryVersionType() {
        return new LibraryVersionType();
    }

    /**
     * Create an instance of {@link FrameSideDataType }
     * 
     */
    public FrameSideDataType createFrameSideDataType() {
        return new FrameSideDataType();
    }

    /**
     * Create an instance of {@link ChapterType }
     * 
     */
    public ChapterType createChapterType() {
        return new ChapterType();
    }

    /**
     * Create an instance of {@link FrameType }
     * 
     */
    public FrameType createFrameType() {
        return new FrameType();
    }

    /**
     * Create an instance of {@link PacketsType }
     * 
     */
    public PacketsType createPacketsType() {
        return new PacketsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FFprobeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ffmpeg.org/schema/ffprobe", name = "ffprobe")
    public JAXBElement<FFprobeType> createFfprobe(FFprobeType value) {
        return new JAXBElement<FFprobeType>(_Ffprobe_QNAME, FFprobeType.class, null, value);
    }

}
