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
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class NutFrameReader<T> implements StdReader<T> {
    private final FrameConsumer frameConsumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NutFrameReader.class);

    public NutFrameReader(FrameConsumer frameConsumer) {
        this.frameConsumer = frameConsumer;
    }

    @Override
    public T read(InputStream stdOut) {
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

        return null;
    }

    public static List<Stream> parseTracks(MainHeader mainHeader, StreamHeader[] streamHeaders) {
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

    private static int[] RGB24_BAND_OFFSETS = {0, 1, 2};

    public static Frame parseFrame(StreamHeader track, NutFrame frame) {
        if (frame == null || frame.data == null || frame.data.length == 0 || frame.eor) {
            return null;
        }

        Frame result = null;

        if (track.streamType == StreamHeader.Type.VIDEO) {
            int width = track.video.width;
            int height = track.video.height;

            // Sometimes if duration limit is specified, ffmpeg creates NutFrame with insufficient data
            if (width * height * 3 != frame.data.length) {
                return null;
            }

            DataBuffer buffer = new DataBufferByte(frame.data, frame.data.length);
            SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width * 3, RGB24_BAND_OFFSETS);
            Raster raster = Raster.createRaster(sampleModel, buffer, null);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            image.setData(raster);

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
