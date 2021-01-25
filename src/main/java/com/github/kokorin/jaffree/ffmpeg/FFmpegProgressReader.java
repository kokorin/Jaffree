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

public class FFmpegProgressReader implements TcpNegotiator {
    private final ProgressListener progressListener;
    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegProgressReader.class);

    public FFmpegProgressReader(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public void negotiate(Socket socket) throws IOException {
        try (InputStream inputStream = socket.getInputStream()) {
            readProgress(inputStream);
        }
    }

    protected void readProgress(InputStream inputStream) throws IOException {
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;

        Long frame = null;
        Double fps = null;
        Double quality = null;
        Double bitrate = null;
        Long totalSize = null;
        Long outTimeMillis = null;
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
                    // intentional fall through, both keys always have the same value
                case "out_time_ms":
                    outTimeMillis = ParseUtil.parseLong(value);
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
                            outTimeMillis,
                            dupFrames,
                            dropFrames,
                            bitrate,
                            speed
                    );
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
