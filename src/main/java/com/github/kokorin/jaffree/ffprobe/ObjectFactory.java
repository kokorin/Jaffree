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
     * Create an instance of {@link FFprobeResult }
     * 
     */
    public FFprobeResult createFfprobeType() {
        return new FFprobeResult();
    }

    /**
     * Create an instance of {@link Log }
     * 
     */
    public Log createLogType() {
        return new Log();
    }

    /**
     * Create an instance of {@link Streams }
     * 
     */
    public Streams createStreamsType() {
        return new Streams();
    }

    /**
     * Create an instance of {@link PacketsAndFrames }
     * 
     */
    public PacketsAndFrames createPacketsAndFramesType() {
        return new PacketsAndFrames();
    }

    /**
     * Create an instance of {@link Error }
     * 
     */
    public Error createErrorType() {
        return new Error();
    }

    /**
     * Create an instance of {@link ProgramVersion }
     * 
     */
    public ProgramVersion createProgramVersionType() {
        return new ProgramVersion();
    }

    /**
     * Create an instance of {@link PixelFormatComponents }
     * 
     */
    public PixelFormatComponents createPixelFormatComponentsType() {
        return new PixelFormatComponents();
    }

    /**
     * Create an instance of {@link Subtitle }
     * 
     */
    public Subtitle createSubtitleType() {
        return new Subtitle();
    }

    /**
     * Create an instance of {@link LibraryVersions }
     * 
     */
    public LibraryVersions createLibraryVersionsType() {
        return new LibraryVersions();
    }

    /**
     * Create an instance of {@link Stream }
     * 
     */
    public Stream createStreamType() {
        return new Stream();
    }

    /**
     * Create an instance of {@link Tag }
     * 
     */
    public Tag createTagType() {
        return new Tag();
    }

    /**
     * Create an instance of {@link Logs }
     * 
     */
    public Logs createLogsType() {
        return new Logs();
    }

    /**
     * Create an instance of {@link Format }
     * 
     */
    public Format createFormatType() {
        return new Format();
    }

    /**
     * Create an instance of {@link PixelFormatComponent }
     * 
     */
    public PixelFormatComponent createPixelFormatComponentType() {
        return new PixelFormatComponent();
    }

    /**
     * Create an instance of {@link PixelFormats }
     * 
     */
    public PixelFormats createPixelFormatsType() {
        return new PixelFormats();
    }

    /**
     * Create an instance of {@link Packet }
     * 
     */
    public Packet createPacketType() {
        return new Packet();
    }

    /**
     * Create an instance of {@link PacketSideData }
     * 
     */
    public PacketSideData createPacketSideDataType() {
        return new PacketSideData();
    }

    /**
     * Create an instance of {@link Program }
     * 
     */
    public Program createProgramType() {
        return new Program();
    }

    /**
     * Create an instance of {@link Chapters }
     * 
     */
    public Chapters createChaptersType() {
        return new Chapters();
    }

    /**
     * Create an instance of {@link PixelFormatFlags }
     * 
     */
    public PixelFormatFlags createPixelFormatFlagsType() {
        return new PixelFormatFlags();
    }

    /**
     * Create an instance of {@link StreamDisposition }
     * 
     */
    public StreamDisposition createStreamDispositionType() {
        return new StreamDisposition();
    }

    /**
     * Create an instance of {@link PacketSideDataList }
     * 
     */
    public PacketSideDataList createPacketSideDataListType() {
        return new PacketSideDataList();
    }

    /**
     * Create an instance of {@link Programs }
     * 
     */
    public Programs createProgramsType() {
        return new Programs();
    }

    /**
     * Create an instance of {@link PixelFormat }
     * 
     */
    public PixelFormat createPixelFormatType() {
        return new PixelFormat();
    }

    /**
     * Create an instance of {@link FrameSideDataList }
     * 
     */
    public FrameSideDataList createFrameSideDataListType() {
        return new FrameSideDataList();
    }

    /**
     * Create an instance of {@link Frames }
     * 
     */
    public Frames createFramesType() {
        return new Frames();
    }

    /**
     * Create an instance of {@link LibraryVersion }
     * 
     */
    public LibraryVersion createLibraryVersionType() {
        return new LibraryVersion();
    }

    /**
     * Create an instance of {@link FrameSideData }
     * 
     */
    public FrameSideData createFrameSideDataType() {
        return new FrameSideData();
    }

    /**
     * Create an instance of {@link Chapter }
     * 
     */
    public Chapter createChapterType() {
        return new Chapter();
    }

    /**
     * Create an instance of {@link Frame }
     * 
     */
    public Frame createFrameType() {
        return new Frame();
    }

    /**
     * Create an instance of {@link Packets }
     * 
     */
    public Packets createPacketsType() {
        return new Packets();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FFprobeResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ffmpeg.org/schema/ffprobe", name = "ffprobe")
    public JAXBElement<FFprobeResult> createFfprobe(FFprobeResult value) {
        return new JAXBElement<FFprobeResult>(_Ffprobe_QNAME, FFprobeResult.class, null, value);
    }

}
