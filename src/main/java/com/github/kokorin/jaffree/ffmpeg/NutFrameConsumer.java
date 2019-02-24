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
import com.github.kokorin.jaffree.nut.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class NutFrameConsumer implements TcpOutput.Consumer {
    private final FrameConsumer frameConsumer;
    private final boolean alpha;

    private static final Logger LOGGER = LoggerFactory.getLogger(NutFrameConsumer.class);

    public NutFrameConsumer(FrameConsumer frameConsumer, boolean alpha) {
        this.frameConsumer = frameConsumer;
        this.alpha = alpha;
    }

    @Override
    public void consumeAndClose(InputStream input) {
        try (Closeable toClose = input) {
            read(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stream", e);
        }
    }

    void read(InputStream input) throws IOException {
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

    private static List<Stream> parseTracks(MainHeader mainHeader, StreamHeader[] streamHeaders) {
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
                    LOGGER.warn("Samplerate denominator is'n equal to 1 (?). This may lead to incorrect audio decoding", samplerate);
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

    private Frame parseFrame(StreamHeader track, NutFrame frame) {
        if (frame == null || frame.data == null || frame.data.length == 0 || frame.eor) {
            return null;
        }

        BufferedImage image = null;
        int[] samples = null;

        if (track.streamType == StreamHeader.Type.VIDEO) {
            int width = track.video.width;
            int height = track.video.height;

            // Sometimes if duration limit is specified, ffmpeg creates NutFrame with insufficient data
            if (!alpha && width * height * 3 != frame.data.length
                    || alpha && width * height * 4 != frame.data.length) {
                return null;
            }

            DataBuffer buffer = new DataBufferByte(frame.data, frame.data.length);
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

            final ColorModel colorModel;
            final WritableRaster raster;

            if (!alpha) {
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
