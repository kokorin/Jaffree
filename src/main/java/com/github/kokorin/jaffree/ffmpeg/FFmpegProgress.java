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

import java.util.concurrent.TimeUnit;

/**
 * {@link FFmpegProgress} contains information about ffmpeg encoding progress.
 */
public class FFmpegProgress {
    private final Long frame;
    private final Double fps;
    private final Double q;
    private final Long size;
    private final Long time;
    private final Long dup;
    private final Long drop;
    private final Double bitrate;
    private final Double speed;

    /**
     * Creates  {@link FFmpegProgress}.
     *
     * @param frame   number of frames
     * @param fps     frames encoded per second
     * @param q       quality of coded frames
     * @param size    current size in bytes
     * @param time    encoded duration
     * @param dup     number of duplicate frames
     * @param drop    number of dropped frames
     * @param bitrate estimated bitrate
     * @param speed   encoding speed
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public FFmpegProgress(final Long frame, final Double fps, final Double q, final Long size,
                          final Long time, final Long dup, final Long drop, final Double bitrate,
                          final Double speed) {
        this.frame = frame;
        this.fps = fps;
        this.q = q;
        this.size = size;
        this.time = time;
        this.dup = dup;
        this.drop = drop;
        this.bitrate = bitrate;
        this.speed = speed;
    }

    /**
     * @return number of frames encoded so far
     */
    public Long getFrame() {
        return frame;
    }

    /**
     * @return frames encoded per second
     */
    public Double getFps() {
        return fps;
    }

    /**
     * @return quality of coded frames
     */
    public Double getQ() {
        return q;
    }

    /**
     * @return size in bytes
     */
    public Long getSize() {
        return size;
    }

    /**
     * @return encoded time in milliseconds
     */
    public Long getTimeMillis() {
        return time;
    }

    /**
     * @param timeUnit time unit of the result
     * @return encoded time in specified {@link TimeUnit}
     */
    public Long getTime(final TimeUnit timeUnit) {
        if (time == null) {
            throw new IllegalArgumentException("TimeUnit must be non null");
        }

        return timeUnit.convert(time, TimeUnit.MILLISECONDS);
    }

    /**
     * @return number of duplicate frames
     */
    public Long getDup() {
        return dup;
    }

    /**
     * @return number of dropped frames
     */
    public Long getDrop() {
        return drop;
    }

    /**
     * @return estimated bitrate
     */
    public Double getBitrate() {
        return bitrate;
    }

    /**
     * Encoding speed represents how many seconds ffmpeg spends on producing 1 second of
     * encoded output.
     *
     * @return encoding speed
     */
    public Double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "FFmpegProgress{" +
                "frame=" + frame +
                ", fps=" + fps +
                ", q=" + q +
                ", size=" + size +
                ", time=" + time +
                ", dup=" + dup +
                ", drop=" + drop +
                ", bitrate=" + bitrate +
                ", speed=" + speed +
                '}';
    }
}
