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

public class Chapter {
    private final DSection section;

    public Chapter(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public int getId() {
        return section.getInteger("id");
    }

    public String getTimeBase() {
        return section.getString("time_base");
    }

    public int getStart() {
        return section.getInteger("start");
    }

    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    public int getEnd() {
        return section.getInteger("end");
    }

    public float getEndTime() {
        return section.getFloat("end_time");
    }
}
