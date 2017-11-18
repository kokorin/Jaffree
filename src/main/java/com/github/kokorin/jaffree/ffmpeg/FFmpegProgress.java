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

public class FFmpegProgress {
    private final long frame;
    private final double fps;
    private final double q;
    private final long size;
    private final long time;
    private final long dup;
    private final long drop;
    private final double bitrate;
    private final double speed;

    public FFmpegProgress(long frame, double fps, double q, long size, long time, long dup, long drop, double bitrate, double speed) {
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

    public long getFrame() {
        return frame;
    }

    public double getFps() {
        return fps;
    }

    public double getQ() {
        return q;
    }

    /**
     * @return size in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * @return time in milliseconds
     */
    public long getTime() {
        return time;
    }

    public long getDup() {
        return dup;
    }

    public long getDrop() {
        return drop;
    }

    public double getBitrate() {
        return bitrate;
    }

    public double getSpeed() {
        return speed;
    }
}
