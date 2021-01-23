/*
 *    Copyright 2017-2021 Denis Kokorin
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
import com.github.kokorin.jaffree.nut.MainHeader;
import com.github.kokorin.jaffree.nut.NutFrame;
import com.github.kokorin.jaffree.nut.NutInputStream;
import com.github.kokorin.jaffree.nut.NutReader;
import com.github.kokorin.jaffree.nut.StreamHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link NutFrameReader} reads InputStream in Nut format and passes parsed frames
 * to {@link FrameConsumer}.
 */
public class NutFrameReader {
    private final FrameConsumer frameConsumer;
    private final boolean alphaChannel;

    private static final int RGB_BYTES_PER_PIXEL = 3;
    private static final int ALPHA_BYTES_PER_PIXEL = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(NutFrameReader.class);

    /**
     * Creates {@link NutFrameReader}.
     *
     * @param frameConsumer frame consumer
     * @param alphaChannel  video stream alpha channel
     */
    public NutFrameReader(final FrameConsumer frameConsumer, final boolean alphaChannel) {
        this.frameConsumer = frameConsumer;
        this.alphaChannel = alphaChannel;
    }

    /**
     * Reads media in Nut format from input stream and closes it.
     *
     * @param input input to read
     */
    public void read(final InputStream input) throws IOException {
        NutInputStream stream = new NutInputStream(input);
        NutReader nutReader = new NutReader(stream);

        MainHeader mainHeader = nutReader.getMainHeader();
        StreamHeader[] streamHeaders = nutReader.getStreamHeaders();
        List<Stream> streams = parseTracks(mainHeader, streamHeaders);
        frameConsumer.consumeStreams(streams);

        LOGGER.debug("Streams: {}", (Object) streamHeaders);

        NutFrame nutFrame;
        while ((nutFrame = nutReader.readFrame()) != null) {
            LOGGER.trace("NutFrame: {}", nutFrame);

            int trackNo = nutFrame.streamId;
            Frame frame = parseFrame(streamHeaders[trackNo], nutFrame);
            LOGGER.trace("Parsed frame: {}", frame);

            if (frame == null) {
                continue;
            }

            frameConsumer.consume(frame);
        }

        frameConsumer.consume(null);
    }

    private static List<Stream> parseTracks(final MainHeader mainHeader,
                                            final StreamHeader[] streamHeaders) {
        List<Stream> result = new ArrayList<>();

        for (StreamHeader streamHeader : streamHeaders) {
            Stream stream = null;
            if (streamHeader.streamType == StreamHeader.Type.VIDEO) {
                stream = new Stream()
                        .setType(Stream.Type.VIDEO)
                        .setWidth(streamHeader.video.width)
                        .setHeight(streamHeader.video.height);
            } else if (streamHeader.streamType == StreamHeader.Type.AUDIO) {
                Rational samplerate = streamHeader.audio.samplerate;
                if (samplerate.denominator != 1) {
                    LOGGER.warn("Samplerate should be integer but it is ({}).", samplerate);
                }

                stream = new Stream()
                        .setType(Stream.Type.AUDIO)
                        .setSampleRate(samplerate.numerator / samplerate.denominator)
                        .setChannels(streamHeader.audio.channelCount);
            }

            if (stream != null) {
                Rational timebase = mainHeader.timeBases[streamHeader.timeBaseId];
                stream.setId(streamHeader.streamId)
                        .setTimebase(timebase.denominator / timebase.numerator);
                result.add(stream);
            }
        }

        return result;
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private Frame parseFrame(final StreamHeader track, final NutFrame frame) {
        if (frame == null || frame.data == null || frame.data.length == 0 || frame.eor) {
            return null;
        }

        BufferedImage image = null;
        int[] samples = null;

        if (track.streamType == StreamHeader.Type.VIDEO) {
            int width = track.video.width;
            int height = track.video.height;

            // Sometimes if duration limit is specified, ffmpeg creates
            // NutFrame with insufficient data
            int pixelCount = width * height;
            if (!alphaChannel && pixelCount * RGB_BYTES_PER_PIXEL != frame.data.length
                    || alphaChannel && pixelCount * ALPHA_BYTES_PER_PIXEL != frame.data.length) {
                return null;
            }

            DataBuffer buffer = new DataBufferByte(frame.data, frame.data.length);
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

            final ColorModel colorModel;
            final WritableRaster raster;

            // TODO extract to BufferedImageUtil
            if (!alphaChannel) {
                int[] nBits = {8, 8, 8};
                int[] bOffs = {2, 1, 0};
                colorModel = new ComponentColorModel(cs, nBits, false, false,
                        Transparency.OPAQUE,
                        DataBuffer.TYPE_BYTE);
                raster = Raster.createInterleavedRaster(buffer,
                        width, height,
                        width * 3, 3,
                        bOffs, null);
            } else {
                int[] nBits = {8, 8, 8, 8};
                int[] bOffs = {3, 2, 1, 0};
                colorModel = new ComponentColorModel(cs, nBits, true, false,
                        Transparency.TRANSLUCENT,
                        DataBuffer.TYPE_BYTE);
                raster = Raster.createInterleavedRaster(buffer,
                        width, height,
                        width * 4, 4,
                        bOffs, null);
            }

            image = new BufferedImage(colorModel, raster, false, null);
        } else if (track.streamType == StreamHeader.Type.AUDIO) {
            ByteBuffer data = ByteBuffer.wrap(frame.data);

            IntBuffer intData = data.asIntBuffer();
            samples = new int[intData.limit()];
            intData.get(samples);
        }

        if (image != null || samples != null) {
            return new Frame(track.streamId, frame.pts, image, samples);
        }

        return null;
    }
}
