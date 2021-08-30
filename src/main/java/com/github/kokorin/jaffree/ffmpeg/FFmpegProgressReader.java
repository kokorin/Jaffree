/*
 *    Copyright 2021 Denis Kokorin
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

import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * {@link FFmpegProgressReader} receives periodical ffmpeg progress report, parses it and passes
 * to {@link ProgressListener}.
 */
public class FFmpegProgressReader implements TcpNegotiator {
    private final ProgressListener progressListener;
    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegProgressReader.class);

    /**
     * Creates {@link FFmpegProgressReader}.
     *
     * @param progressListener progress listener to pass parsed progress
     */
    public FFmpegProgressReader(final ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void negotiate(final Socket socket) throws IOException {
        try (InputStream inputStream = socket.getInputStream()) {
            readProgress(inputStream);
        }
    }

    /**
     * Reads periodical ffmpeg progress report, parses it and passes to {@link ProgressListener}.
     *
     * @param inputStream input stream to read from
     * @throws IOException if any IO error
     */
    protected void readProgress(final InputStream inputStream) throws IOException {
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;

        Long frame = null;
        Double fps = null;
        Double quality = null;
        Double bitrate = null;
        Long totalSize = null;
        Long outTimeMicros = null;
        Long dupFrames = null;
        Long dropFrames = null;
        Double speed = null;

        LOGGER.debug("Reading encoding progress");

        while ((line = lineReader.readLine()) != null) {
            LOGGER.trace("Line read: {}", line);

            String[] keyValue = line.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "frame":
                    frame = ParseUtil.parseLong(value);
                    break;
                case "fps":
                    fps = ParseUtil.parseDouble(value);
                    break;
                case "bitrate":
                    bitrate = ParseUtil.parseBitrateInKBits(value);
                    break;
                case "total_size":
                    totalSize = ParseUtil.parseLong(value);
                    break;
                case "out_time_us":
                    // intentional fall through
                case "out_time_ms":
                    // us - is common abbreviation of microseconds, ms - for milliseconds
                    // ffmpeg passes time with "ms" suffix in microseconds
                    // see https://trac.ffmpeg.org/ticket/7345
                    // out_time_us added in 2018 so we have to parse both properties to support
                    // older versions. out_time_ms may be removed from ffmpeg.
                    outTimeMicros = ParseUtil.parseLong(value);
                    break;
                case "out_time":
                    // ignored, no need to parse since we have time in millis
                    break;
                case "dup_frames":
                    dupFrames = ParseUtil.parseLong(value);
                    break;
                case "drop_frames":
                    dropFrames = ParseUtil.parseLong(value);
                    break;
                case "speed":
                    speed = ParseUtil.parseSpeed(value);
                    break;
                case "progress":
                    FFmpegProgress progress = new FFmpegProgress(
                            frame,
                            fps,
                            quality,
                            totalSize,
                            outTimeMicros,
                            dupFrames,
                            dropFrames,
                            bitrate,
                            speed
                    );
                    frame = null;
                    fps = null;
                    quality = null;
                    bitrate = null;
                    totalSize = null;
                    outTimeMicros = null;
                    dupFrames = null;
                    dropFrames = null;
                    speed = null;
                    progressListener.onProgress(progress);
                    break;
                default:
                    if (key.startsWith("stream_")) {
                        // TODO progress output can contain several stream quality parameters,
                        // one per each output video stream:
                        // stream_0_0_q=29.0
                        // stream_0_1_q=29.0
                        quality = ParseUtil.parseDouble(value);
                        break;
                    }

                    LOGGER.info("Unknown property: {}", key);
                    break;
            }

        }
    }
}
