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
import com.github.kokorin.jaffree.process.StdWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.List;

public class NutFrameWriter implements StdWriter {
    private final FrameProducer producer;

    // StreamHeader{streamId=0, streamType=VIDEO, fourcc=[82, 71, 66, 24]}
    // StreamHeader{streamId=1, streamType=AUDIO, fourcc=[32, 68, 83, 80]}
    private static final byte[] FOURCC_RGB24 = {(byte) 82, (byte) 71, (byte) 66, (byte) 24};
    // private static final byte[] FOURCC_RGB24 = {(byte) 'r', (byte) 'g', (byte) 'b', (byte) 24};
    private static final byte[] FOURCC_PCM_S32BE = {(byte) 32, (byte) 68, (byte) 83, (byte) 80};

    private static int[] RGB24_BAND_OFFSETS = {0, 1, 2};

    public NutFrameWriter(FrameProducer producer) {
        this.producer = producer;
    }

    @Override
    public void write(OutputStream stdIn) {
        try {
            NutWriter writer = new NutWriter(new NutOutputStream(stdIn));
            write(writer);
            writer.writeFooter();
        } catch (IOException e) {
            throw new RuntimeException("Can't write frames", e);
        } catch (Exception e) {
            throw new RuntimeException("Can't writeFooter stream", e);
        }
    }

    // package private for test
    void write(NutWriter writer) throws IOException {

        List<Stream> tracks = producer.produceStreams();
        StreamHeader[] streamHeaders = new StreamHeader[tracks.size()];
        Rational[] timebases = new Rational[tracks.size()];

        for (int i = 0; i < streamHeaders.length; i++) {
            Stream track = tracks.get(i);
            if (track.getId() != i) {
                throw new RuntimeException("Track ids must start with 0 and increase by 1 subsequently!");
            }
            final StreamHeader streamHeader;

            switch (track.getType()) {
                case VIDEO:
                    streamHeader = new StreamHeader(
                            track.getId(),
                            StreamHeader.Type.VIDEO,
                            FOURCC_RGB24,
                            i,
                            0,
                            60_000,
                            0,
                            EnumSet.noneOf(StreamHeader.Flag.class),
                            new byte[0],
                            new StreamHeader.Video(
                                    track.getWidth(),
                                    track.getHeight(),
                                    1,
                                    1,
                                    StreamHeader.ColourspaceType.UNKNOWN
                            ),
                            null
                    );
                    break;
                case AUDIO:
                    streamHeader = new StreamHeader(
                            track.getId(),
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
                                    new Rational(track.getSampleRate(), 1),
                                    track.getChannels()
                            )
                    );
                    break;
                default:
                    throw new RuntimeException("Unknown Track Type: " + track.getType());
            }

            streamHeaders[i] = streamHeader;
            timebases[i] = new Rational(1, track.getTimebase());
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
            final byte[] data;
            StreamHeader streamHeader = streamHeaders[frame.getStreamId()];
            switch (streamHeader.streamType) {
                case VIDEO:
                    data = new byte[streamHeader.video.width * streamHeader.video.width * 3];
                    // TODO will it work faster if we use Raster and Buffers?
                    for (int y = 0; y < streamHeader.video.height; y++) {
                        for (int x = 0; x < streamHeader.video.width; x++) {
                            int rgb = frame.getImage().getRGB(x, y);
                            int position = 3 * (x + y * streamHeader.video.width);
                            data[position] = (byte) ((rgb >> 16) & 0xFF);
                            data[position + 1] = (byte) ((rgb >> 8) & 0xFF);
                            data[position + 2] = (byte) (rgb & 0xFF);
                        }
                    }
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

            writer.writeFrame(nutFrame);
        }
    }
}
