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

import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.DTag;

import java.util.List;

public class Format {
    private final DSection section;

    public Format(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    // TODO Does Format contain any tags?
    public List<Tag> getTags() {
        return section.getTag("TAG", "TAGS").getValues(DTag.TAG_CONVERTER);
    }
    // TODO Does Format contain any tags?
    public String getTag(String name) {
        return section.getTag("TAG", "TAGS").getString(name);
    }

    public String getFilename() {
        return section.getString("filename");
    }

    public int getNbStreams() {
        return section.getInteger("nb_streams");
    }

    public int getNbPrograms() {
        return section.getInteger("nb_programs");
    }

    public String getFormatName() {
        return section.getString("format_name");
    }

    public String getFormatLongName() {
        return section.getString("format_long_name");
    }

    // TODO: getter with TimeUnit?
    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    // TODO: getter with TimeUnit?
    public Float getDuration() {
        return section.getFloat("duration");
    }

    public Long getSize() {
        return section.getLong("size");
    }

    public Long getBitRate() {
        return section.getLong("bit_rate");
    }

    public Integer getProbeScore() {
        return section.getInteger("probe_score");
    }
}
