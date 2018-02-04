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

import com.github.kokorin.jaffree.nut.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class NutFrameReader implements Runnable {
    private final FrameConsumer frameConsumer;
    private final boolean alpha;
    private final ServerSocket serverSocket;

    private static final Logger LOGGER = LoggerFactory.getLogger(NutFrameReader.class);

    public NutFrameReader(FrameConsumer frameConsumer, boolean alpha, ServerSocket serverSocket) {
        this.frameConsumer = frameConsumer;
        this.alpha = alpha;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = this.serverSocket;
             Socket socket = serverSocket.accept();
             InputStream input = socket.getInputStream()) {
            read(input);
        } catch (IOException e) {
            throw  new RuntimeException("Failed to read from socket " + serverSocket, e);
        }
    }

    // package-private for test
    void read(InputStream stdOut) {
        NutInputStream stream = new NutInputStream(stdOut);
        NutReader nutReader = new NutReader(stream);

        try {
            MainHeader mainHeader = nutReader.getMainHeader();
            StreamHeader[] streamHeaders = nutReader.getStreamHeaders();
            List<Stream> tracks = parseTracks(mainHeader, streamHeaders);
            frameConsumer.consumeStreams(tracks);

            LOGGER.debug("Tracks: {}", streamHeaders);

            NutFrame nutFrame;
            while ((nutFrame = nutReader.readFrame()) != null) {
                LOGGER.trace("NutFrame: {}", nutFrame);

                int trackNo = nutFrame.streamId;
                Frame frame = parseFrame(streamHeaders[trackNo], nutFrame);
                if (frame == null) {
                    continue;
                }

                frameConsumer.consume(frame);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stream", e);
        }

        frameConsumer.consume(null);
    }

    private static List<Stream> parseTracks(MainHeader mainHeader, StreamHeader[] streamHeaders) {
        List<Stream> result = new ArrayList<>();

        for (StreamHeader streamHeader : streamHeaders) {
            Stream track = null;
            if (streamHeader.streamType == StreamHeader.Type.VIDEO) {
                track = new Stream()
                        .setType(Stream.Type.VIDEO)
                        .setWidth(streamHeader.video.width)
                        .setHeight(streamHeader.video.height);
            } else if (streamHeader.streamType == StreamHeader.Type.AUDIO) {
                Rational samplerate = streamHeader.audio.samplerate;
                if (samplerate.denominator != 1) {
                    LOGGER.warn("Samplerate denominator is'n equal to 1 (?). This may lead to incorrect audio decoding", samplerate);
                }

                track = new Stream()
                        .setType(Stream.Type.AUDIO)
                        .setSampleRate(samplerate.numerator / samplerate.denominator)
                        .setChannels(streamHeader.audio.channelCount);
            }

            if (track != null) {
                Rational timebase = mainHeader.timeBases[streamHeader.timeBaseId];
                track.setId(streamHeader.streamId)
                        .setTimebase(timebase.denominator / timebase.numerator);
                result.add(track);
            }
        }

        return result;
    }

    private Frame parseFrame(StreamHeader track, NutFrame frame) {
        if (frame == null || frame.data == null || frame.data.length == 0 || frame.eor) {
            return null;
        }

        Frame result = null;

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

            BufferedImage image = new BufferedImage(colorModel, raster, false, null);

            result = new Frame()
                    .setImage(image);
        } else if (track.streamType == StreamHeader.Type.AUDIO) {
            ByteBuffer data = ByteBuffer.wrap(frame.data);

            IntBuffer intData = data.asIntBuffer();
            int[] samples = new int[intData.limit()];
            intData.get(samples);

            result = new Frame()
                    .setSamples(samples);
        }

        if (result != null) {
            result.setStreamId(track.streamId)
                    .setPts(frame.pts);
        }

        return result;
    }
}
