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
 * Chapter description.
 */
public class Chapter {
    private final DSection section;

    /**
     * Creates {@link Chapter} description based on provided data sections.
     * @param section data section
     */
    public Chapter(final DSection section) {
        this.section = section;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for the current Chapter.
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
     * Returns tags for this Chapter.
     *
     * @return tags
     */
    public List<Tag> getTags() {
        return section.getTag("TAG", "TAGS").getValues(DTag.TAG_CONVERTER);
    }

    public String getTag(String name) {
        return section.getTag("TAG", "TAGS").getString(name);
    }

    /**
     * Returns Chapter ID.
     *
     * @return id
     */
    public int getId() {
        return section.getInteger("id");
    }

    /**
     * Returns Chapter time base.
     *
     * @return time base
     */
    // TODO: make it integer?
    public String getTimeBase() {
        return section.getString("time_base");
    }

    /**
     * Returns chapter start PTS (in time base).
     *
     * @return start PTS
     * @see #getTimeBase()
     */
    public int getStart() {
        return section.getInteger("start");
    }

    /**
     * Returns Chapter start time.
     *
     * @return start time
     */
    // TODO: check if the result is return in seconds or millis
    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    /**
     * Returns chapter end PTS (in time base).
     *
     * @return end PTS
     * @see #getTimeBase()
     */
    public int getEnd() {
        return section.getInteger("end");
    }

    /**
     * Returns Chapter end time.
     *
     * @return end time
     */
    // TODO: check if the result is return in seconds or millis
    public float getEndTime() {
        return section.getFloat("end_time");
    }
}
