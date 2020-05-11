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

/**
 * Format description.
 */
public class Format {
    private final DSection section;

    /**
     * Creates {@link Format} description based on provided data sections.
     *
     * @param section data section
     */
    public Format(final DSection section) {
        this.section = section;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for current Format.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public DSection getSection() {
        return section;
    }


    /**
     * Returns tags for this Format.
     *
     * @return tags
     */
    // TODO Does Format contain any tags?
    public List<Tag> getTags() {
        return section.getTag("TAG", "TAGS").getValues(DTag.TAG_CONVERTER);
    }
    // TODO Does Format contain any tags?
    public String getTag(String name) {
        return section.getTag("TAG", "TAGS").getString(name);
    }

    /**
     * @return file name
     */
    public String getFilename() {
        return section.getString("filename");
    }

    /**
     * @return number of streams
     */
    public int getNbStreams() {
        return section.getInteger("nb_streams");
    }

    /**
     * @return number of programs
     */
    public int getNbPrograms() {
        return section.getInteger("nb_programs");
    }

    /**
     * @return format name
     */
    public String getFormatName() {
        return section.getString("format_name");
    }

    /**
     * @return format long name
     */
    public String getFormatLongName() {
        return section.getString("format_long_name");
    }

    /**
     * @return media start time in seconds
     */
    // TODO: getter with TimeUnit?
    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    /**
     * @return media duration in seconds
     */
    // TODO: getter with TimeUnit?
    public Float getDuration() {
        return section.getFloat("duration");
    }

    /**
     * @return media size in bytes
     */
    public Long getSize() {
        return section.getLong("size");
    }

    /**
     * @return media bitrate in bits per second
     */
    public Long getBitRate() {
        return section.getLong("bit_rate");
    }

    /**
     * Returns detected format score.
     * <p>
     * FFprobe assigns corresponding probe score to each known format during input analysis.
     * The return format by ffprobe is the one with highest score.
     * <p>
     * Higher values (up to 100) mean higher probability of correct format detection.
     *
     * @return format probe score
     */
    public Integer getProbeScore() {
        return section.getInteger("probe_score");
    }
}
