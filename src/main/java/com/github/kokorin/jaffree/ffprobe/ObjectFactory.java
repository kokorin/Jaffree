
package com.github.kokorin.jaffree.ffprobe;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.github.kokorin.jaffree.ffprobe package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.github.kokorin.jaffree.ffprobe
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FFprobeResult }
     * 
     */
    public FFprobeResult createFFprobeResult() {
        return null;
    }

    /**
     * Create an instance of {@link Packet }
     * 
     */
    public Packet createPacket() {
        return new Packet();
    }

    /**
     * Create an instance of {@link PacketSideData }
     * 
     */
    public PacketSideData createPacketSideData() {
        return new PacketSideData();
    }

    /**
     * Create an instance of {@link Frame }
     * 
     */
    public Frame createFrame() {
        return new Frame();
    }

    /**
     * Create an instance of {@link Log }
     * 
     */
    public Log createLog() {
        return new Log();
    }

    /**
     * Create an instance of {@link FrameSideData }
     * 
     */
    public FrameSideData createFrameSideData() {
        return new FrameSideData();
    }

    /**
     * Create an instance of {@link Subtitle }
     * 
     */
    public Subtitle createSubtitle() {
        return new Subtitle();
    }

    /**
     * Create an instance of {@link StreamDisposition }
     * 
     */
    public StreamDisposition createStreamDisposition() {
        return null;
    }

    /**
     * Create an instance of {@link Stream }
     * 
     */
    public Stream createStream() {
        return new Stream(null);
    }

    /**
     * Create an instance of {@link Program }
     * 
     */
    public Program createProgram() {
        return new Program();
    }

    /**
     * Create an instance of {@link Format }
     * 
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link Tag }
     * 
     */
    public Tag createTag() {
        return null;
    }

    /**
     * Create an instance of {@link Error }
     * 
     */
    public Error createError() {
        return new Error();
    }

    /**
     * Create an instance of {@link ProgramVersion }
     * 
     */
    public ProgramVersion createProgramVersion() {
        return new ProgramVersion();
    }

    /**
     * Create an instance of {@link Chapter }
     * 
     */
    public Chapter createChapter() {
        return new Chapter();
    }

    /**
     * Create an instance of {@link LibraryVersion }
     * 
     */
    public LibraryVersion createLibraryVersion() {
        return new LibraryVersion();
    }

    /**
     * Create an instance of {@link PixelFormatFlags }
     * 
     */
    public PixelFormatFlags createPixelFormatFlags() {
        return new PixelFormatFlags();
    }

    /**
     * Create an instance of {@link PixelFormatComponent }
     * 
     */
    public PixelFormatComponent createPixelFormatComponent() {
        return new PixelFormatComponent();
    }

    /**
     * Create an instance of {@link PixelFormat }
     * 
     */
    public PixelFormat createPixelFormat() {
        return new PixelFormat();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FFprobeResult }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link FFprobeResult }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.ffmpeg.org/schema/ffprobe", name = "ffprobe")
    public JAXBElement<FFprobeResult> createFfprobe(FFprobeResult value) {
        return new JAXBElement<FFprobeResult>(_Ffprobe_QNAME, FFprobeResult.class, null, value);
    }

}
