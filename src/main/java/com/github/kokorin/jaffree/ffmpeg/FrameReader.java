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

import com.github.kokorin.jaffree.matroska.InputStreamSource;
import com.github.kokorin.jaffree.process.StdReader;
import org.ebml.io.DataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileFrame;
import org.ebml.matroska.MatroskaFileTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class FrameReader<T> implements StdReader<T> {
    private final FrameConsumer frameConsumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameReader.class);

    public FrameReader(FrameConsumer frameConsumer) {
        this.frameConsumer = frameConsumer;
    }

    @Override
    public T read(InputStream stdOut) {
                /*byte[] bytes = new byte[1_000_000];
                try {
                    while (stdOut.read(bytes) != -1) {
                        LOGGER.warn("Read bytes");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

        DataSource source = new InputStreamSource(stdOut);
        MatroskaFile mkv = new MatroskaFile(source);
        mkv.readFile();
        MatroskaFileTrack[] tracks = mkv.getTrackList();

        LOGGER.debug("Tracks: {}", tracks);

        MatroskaFileFrame mkvFrame;
        while ((mkvFrame = mkv.getNextFrame()) != null) {
            LOGGER.debug("Frame: {}", mkvFrame);
            frameConsumer.consume(new Frame() {
            });
        }

        return null;
    }
}
