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

    public FFmpegProgress(Long frame, Double fps, Double q, Long size, Long time, Long dup, Long drop, Double bitrate, Double speed) {
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

    public Long getFrame() {
        return frame;
    }

    public Double getFps() {
        return fps;
    }

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
     * @return time in milliseconds
     */
    public Long getTimeMillis() {
        return time;
    }

    public Long getTime(TimeUnit timeUnit) {
        if (time == null) {
            return null;
        }

        return timeUnit.convert(time, TimeUnit.MILLISECONDS);
    }


    public Long getDup() {
        return dup;
    }

    public Long getDrop() {
        return drop;
    }

    public Double getBitrate() {
        return bitrate;
    }

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
