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

import com.github.kokorin.jaffree.process.StdWriter;
import org.ebml.io.DataWriter;
import org.ebml.matroska.MatroskaFileFrame;
import org.ebml.matroska.MatroskaFileTrack;
import org.ebml.matroska.MatroskaFileTrack.MatroskaVideoTrack;
import org.ebml.matroska.MatroskaFileWriter;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class FrameWriter implements StdWriter {
    private final FrameProducer producer;

    // https://www.fourcc.org/yuv.php
    private static final byte[] I420 = {(byte) 0x49, (byte) 0x34, (byte) 0x32, (byte) 0x30};

    public FrameWriter(FrameProducer producer) {
        this.producer = producer;
    }

    @Override
    public void write(OutputStream stdIn) {
        write(new OutputStreamWriter(stdIn));
    }

    // package private for test
    void write(DataWriter writer) {
        MatroskaFileWriter mkv = new MatroskaFileWriter(writer);

        List<Track> tracks = producer.produceTracks();
        for (Track track : tracks) {
            MatroskaFileTrack mkvTrack = null;

            if (track.getType() == Track.Type.VIDEO) {
                mkvTrack = new MatroskaFileTrack();
                mkvTrack.setTrackType(MatroskaFileTrack.TrackType.VIDEO);
                mkvTrack.setCodecID("V_UNCOMPRESSED");

                MatroskaVideoTrack videoTrack = new MatroskaVideoTrack();
                videoTrack.setPixelWidth((short) track.getWidth());
                videoTrack.setPixelHeight((short) track.getHeight());
                videoTrack.setColourSpace(I420);

                mkvTrack.setVideo(videoTrack);
            }

            if (mkvTrack != null) {
                mkvTrack.setTrackNo(track.getId());
                String title = track.getTitle();
                if (title == null) {
                    title = "unnamed";
                }
                mkvTrack.setName(title);

                mkv.addTrack(mkvTrack);
            }
        }

        Frame frame;
        while ((frame = producer.produce()) != null) {
            MatroskaFileFrame mkvFrame = new MatroskaFileFrame();
            mkvFrame.setTrackNo(frame.getTrack());
            mkvFrame.setTimecode(frame.getTimecode());

            if (frame instanceof VideoFrame) {
                VideoFrame videoFrame = (VideoFrame) frame;
                ByteBuffer data = composeDate(videoFrame.getImage());
                mkvFrame.setData(data);
            }

            mkv.addFrame(mkvFrame);
        }

        mkv.close();
    }

    public static ByteBuffer composeDate(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer buffer = ByteBuffer.allocate(width * height * 3 / 2 + 4);
        //ByteBuffer buffer = ByteBuffer.allocate(width * height * 3 / 2);
        //0x30 32 34 49
        buffer.put((byte) 0x30);
        buffer.put((byte) 0x32);
        buffer.put((byte) 0x34);
        buffer.put((byte) 0x49);

        ByteBuffer data = buffer.slice();

        int total = width * height;
        byte[] yuv = new byte[3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                rgbToYuv(rgb, yuv);

                data.put(y * width + x, yuv[0]);
                data.put((y / 2) * (width / 2) + (x / 2) + total, yuv[1]);
                data.put((y / 2) * (width / 2) + (x / 2) + total + (total / 4), yuv[2]);
            }
        }

        buffer.flip();
        return buffer;

    }

    public static void rgbToYuv(int rgb, byte[] yuv) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        int y = 66 * r + 129 * g + 25 * b;
        int u = -38 * r - 74 * g + 112 * b;
        int v = 112 * r - 94 * g - 18 * b;
        y = (y + 128) >> 8;
        u = (u + 128) >> 8;
        v = (v + 128) >> 8;

        yuv[0] = (byte) (y + 16);
        yuv[1] = (byte) (u + 128);
        yuv[2] = (byte) (v + 128);
    }
}
