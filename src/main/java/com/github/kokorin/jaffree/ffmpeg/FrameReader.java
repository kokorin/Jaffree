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

import com.github.kokorin.jaffree.process.StdReader;
import org.ebml.io.DataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileFrame;
import org.ebml.matroska.MatroskaFileTrack;
import org.ebml.matroska.MatroskaFileTrack.MatroskaVideoTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FrameReader<T> implements StdReader<T> {
    private final FrameConsumer frameConsumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameReader.class);

    public FrameReader(FrameConsumer frameConsumer) {
        this.frameConsumer = frameConsumer;
    }

    @Override
    public T read(InputStream stdOut) {
        DataSource source = new InputStreamSource(stdOut);
        MatroskaFile mkv = new MatroskaFile(source);
        mkv.readFile();
        MatroskaFileTrack[] tracks = mkv.getTrackList();

        LOGGER.debug("Tracks: {}", tracks);

        MatroskaFileFrame mkvFrame;
        while ((mkvFrame = mkv.getNextFrame()) != null) {
            LOGGER.trace("Frame: {}", mkvFrame);

            int trackNo = mkvFrame.getTrackNo();
            Frame frame = parseFrame(mkv.getTrack(trackNo), mkvFrame);
            if (frame == null) {
                continue;
            }

            frameConsumer.consume(frame);
        }

        return null;
    }

    public static Frame parseFrame(MatroskaFileTrack track, MatroskaFileFrame frame) {
        Frame result = null;

        if (track.getTrackType() == MatroskaFileTrack.TrackType.VIDEO) {
            MatroskaVideoTrack videoTrack = track.getVideo();
            // frame data contains frame header and payload
            // current position is right after header
            // slice() will create a buffer with only payload
            ByteBuffer data = frame.getData().slice();
            BufferedImage image = parseImage(data, videoTrack.getPixelWidth(), videoTrack.getPixelHeight());
            VideoFrame videoResult = new VideoFrame();
            videoResult.setImage(image);
            result = videoResult;
        }

        if (result != null) {
            result.setTrack(track.getTrackNo());
            result.setTimecode(frame.getTimecode());
        }

        return result;
    }

    // https://en.wikipedia.org/wiki/YUV
    public static BufferedImage parseImage(ByteBuffer buffer, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int total = width * height;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte Y = buffer.get(y * width + x);
                byte U = buffer.get((y / 2) * (width / 2) + (x / 2) + total);
                byte V = buffer.get((y / 2) * (width / 2) + (x / 2) + total + (total / 4));

                int rgb = yuvToRgb(Y, U, V);

                result.setRGB(x, y, rgb);
            }
        }

        return result;
    }

    public static int yuvToRgb(byte y, byte u, byte v) {
        final int c = (y & 0xFF) - 16;
        final int d = (u & 0xFF) - 128;
        final int e = (v & 0xFF) - 128;

        final int r = (298 * c + 409 * e + 128) >> 8;
        final int g = (298 * c - 100 * d - 208 * e + 128) >> 8;
        final int b = (298 * c + 516 * d + 128) >> 8;

        int rc = Math.max(0, Math.min(r, 0xFF));
        int gc = Math.max(0, Math.min(g, 0xFF));
        int bc = Math.max(0, Math.min(b, 0xFF));
        //return r << 16 + g << 8 + b;
        return (rc << 16) + (gc << 8) + bc;
    }
}
