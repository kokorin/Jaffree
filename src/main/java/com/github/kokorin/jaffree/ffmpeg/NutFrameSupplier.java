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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.network.OutputStreamSupplier;
import com.github.kokorin.jaffree.nut.DataItem;
import com.github.kokorin.jaffree.nut.FrameCode;
import com.github.kokorin.jaffree.nut.Info;
import com.github.kokorin.jaffree.nut.NutFrame;
import com.github.kokorin.jaffree.nut.NutOutputStream;
import com.github.kokorin.jaffree.nut.NutWriter;
import com.github.kokorin.jaffree.nut.StreamHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * {@link OutputStreamSupplier} implementation which allows writing Nut format.
 */
public class NutFrameSupplier implements OutputStreamSupplier {
    private final FrameProducer producer;
    private final boolean alpha;
    private final Long frameOrderingBufferMillis;

    private static final byte[] FOURCC_ABGR = {'A', 'B', 'G', 'R'};
    private static final byte[] FOURCC_BGR24 = {'B', 'G', 'R', 24};
    //PCM Signed Differential?
    private static final byte[] FOURCC_PCM_S32BE = {32, 'D', 'S', 'P'};


    private static final Logger LOGGER = LoggerFactory.getLogger(NutFrameSupplier.class);

    /**
     * Creates {@link NutFrameSupplier} with default frame reordering buffer length.
     *
     * @param producer frame producer
     * @param alpha    video stream alpha channel
     */
    public NutFrameSupplier(final FrameProducer producer, final boolean alpha) {
        this(producer, alpha, null);
    }

    /**
     * Creates {@link NutFrameSupplier}.
     *
     * @param producer                  frame producer
     * @param alpha                     video stream alpha channel
     * @param frameOrderingBufferMillis frame reordering buffer length
     */
    public NutFrameSupplier(final FrameProducer producer, final boolean alpha,
                            final Long frameOrderingBufferMillis) {
        this.producer = producer;
        this.alpha = alpha;
        this.frameOrderingBufferMillis = frameOrderingBufferMillis;
    }


    /**
     * Writes media in Nut format to output stream and closes it.
     *
     * @param outputStream OutputStream output stream to write to
     */
    @Override
    public void supply(final OutputStream outputStream) throws IOException {
        NutWriter writer = new NutWriter(new NutOutputStream(outputStream));
        if (frameOrderingBufferMillis != null) {
            writer.setFrameOrderingBufferMillis(frameOrderingBufferMillis);
        }
        write(writer);
        writer.writeFooter();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private void write(final NutWriter writer) throws IOException {
        List<Stream> tracks = producer.produceStreams();
        LOGGER.debug("Streams: {}", tracks.toArray());

        StreamHeader[] streamHeaders = new StreamHeader[tracks.size()];
        Rational[] timebases = new Rational[tracks.size()];

        for (int i = 0; i < streamHeaders.length; i++) {
            Stream stream = tracks.get(i);
            if (stream.getId() != i) {
                throw new RuntimeException("Stream ids must start with 0 and "
                        + "increase by 1 subsequently!");
            }
            final StreamHeader streamHeader;

            Objects.requireNonNull(stream.getType(), "Stream type must be specified");
            Objects.requireNonNull(stream.getTimebase(), "Stream timebase must be specified");

            switch (stream.getType()) {
                case VIDEO:
                    Objects.requireNonNull(stream.getWidth(), "Width must be specified");
                    Objects.requireNonNull(stream.getHeight(), "Height must be specified");
                    streamHeader = new StreamHeader(
                            stream.getId(),
                            StreamHeader.Type.VIDEO,
                            alpha ? FOURCC_ABGR : FOURCC_BGR24,
                            i,
                            0,
                            60_000,
                            0,
                            EnumSet.noneOf(StreamHeader.Flag.class),
                            new byte[0],
                            new StreamHeader.Video(
                                    stream.getWidth(),
                                    stream.getHeight(),
                                    1,
                                    1,
                                    StreamHeader.ColourspaceType.UNKNOWN
                            ),
                            null
                    );
                    break;
                case AUDIO:
                    Objects.requireNonNull(stream.getSampleRate(),
                            "Samplerate must be specified");
                    Objects.requireNonNull(stream.getChannels(),
                            "Number of channels must be specified");
                    streamHeader = new StreamHeader(
                            stream.getId(),
                            StreamHeader.Type.AUDIO,
                            FOURCC_PCM_S32BE,
                            i,
                            0,
                            60_000,
                            0,
                            EnumSet.noneOf(StreamHeader.Flag.class),
                            new byte[0],
                            null,
                            new StreamHeader.Audio(
                                    new Rational(stream.getSampleRate(), 1),
                                    stream.getChannels()
                            )
                    );
                    break;
                default:
                    throw new RuntimeException("Unknown Track Type: " + stream.getType());
            }

            streamHeaders[i] = streamHeader;
            timebases[i] = new Rational(1, stream.getTimebase());
        }

        int framecodesLength = 256;
        FrameCode[] frameCodes = new FrameCode[framecodesLength];
        frameCodes[0] = FrameCode.INVALID;
        frameCodes[1] = new FrameCode(
                EnumSet.of(FrameCode.Flag.CODED_FLAGS),
                0,
                1,
                0,
                0,
                0,
                0,
                0
        );
        for (int i = 2; i < framecodesLength; i++) {
            frameCodes[i] = FrameCode.INVALID;
        }

        writer.setMainHeader(tracks.size(), Short.MAX_VALUE, timebases, frameCodes);
        writer.setStreamHeaders(streamHeaders);
        writer.setInfos(new Info[0]);

        Frame frame;
        while ((frame = producer.produce()) != null) {
            LOGGER.trace("Frame: {}", frame);

            final byte[] data;
            StreamHeader streamHeader = streamHeaders[frame.getStreamId()];
            switch (streamHeader.streamType) {
                case VIDEO:
                    BufferedImage image = frame.getImage();

                    if (alpha && BufferedImage.TYPE_4BYTE_ABGR != image.getType()) {
                        throw new RuntimeException("Type of BufferedImage must be TYPE_4BYTE_ABGR");
                    }
                    if (!alpha && BufferedImage.TYPE_3BYTE_BGR != image.getType()) {
                        throw new RuntimeException("Type of BufferedImage must be TYPE_3BYTE_BGR");
                    }

                    data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                    break;

                case AUDIO:
                    data = new byte[frame.getSamples().length * 4];
                    ByteBuffer.wrap(data).asIntBuffer().put(frame.getSamples());
                    break;

                default:
                    throw new RuntimeException("Unexpected track: " + frame.getStreamId());
            }

            NutFrame nutFrame = new NutFrame(
                    frame.getStreamId(),
                    frame.getPts(),
                    data,
                    new DataItem[0],
                    new DataItem[0],
                    true,
                    false
            );

            LOGGER.trace("NutFrame: {}", nutFrame);
            writer.writeFrame(nutFrame);
        }
    }
}
