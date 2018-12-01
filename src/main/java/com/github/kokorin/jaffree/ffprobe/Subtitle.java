/*
 *    Copyright  2018 Denis Kokorin
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
import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class Subtitle {
    private final DSection section;

    public Subtitle(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public StreamType getMediaType() {
        return section.getStreamType("media_type");
    }

    public Long getPts() {
        return section.getLong("pts");
    }

    public Float getPtsTime() {
        return section.getFloat("pts_time");
    }

    public Integer getFormat() {
        return section.getInteger("format");
    }

    public Integer getStartDisplayTime() {
        return section.getInteger("start_display_time");
    }

    public Integer getEndDisplayTime() {
        return section.getInteger("end_display_time");
    }

    public Integer getNumRects() {
        return section.getInteger("num_rects");
    }
}
