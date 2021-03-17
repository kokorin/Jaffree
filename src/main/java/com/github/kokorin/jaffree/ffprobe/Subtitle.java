/*
 *    Copyright 2018-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

public class Subtitle {
    private final ProbeData probeData;

    public Subtitle(ProbeData probeData) {
        this.probeData = probeData;
    }

    public ProbeData getProbeData() {
        return probeData;
    }

    public StreamType getMediaType() {
        return probeData.getStreamType("media_type");
    }

    public Long getPts() {
        return probeData.getLong("pts");
    }

    public Float getPtsTime() {
        return probeData.getFloat("pts_time");
    }

    public Integer getFormat() {
        return probeData.getInteger("format");
    }

    public Integer getStartDisplayTime() {
        return probeData.getInteger("start_display_time");
    }

    public Integer getEndDisplayTime() {
        return probeData.getInteger("end_display_time");
    }

    public Integer getNumRects() {
        return probeData.getInteger("num_rects");
    }
}
