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

public class Program {
    private final DSection section;

    public Program(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    // TODO add Program Streams test
    public List<Stream> getStreams() {
        return section.getSections("STREAM", DSection.STREAM_CONVERTER);
    }

    public int getProgramId() {
        return section.getInteger("program_id");
    }

    public int getProgramNum() {
        return section.getInteger("program_num");
    }

    public int getNbStreams() {
        return section.getInteger("nb_streams");
    }

    public Float getStartTime() {
        return section.getFloat("start_time");
    }

    public Long getStartPts() {
        return section.getLong("start_pts");
    }

    public Float getEndTime() {
        return section.getFloat("end_time");
    }

    public Long getEndPts() {
        return section.getLong("end_pts");
    }

    public int getPmtPid() {
        return section.getInteger("pmt_pid");
    }

    public int getPcrPid() {
        return section.getInteger("pcr_pid");
    }
}
