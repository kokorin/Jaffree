
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.DTag;

import java.util.Collections;
import java.util.List;

public class Program {
    private final DSection section;

    public Program(DSection section) {
        this.section = section;
    }

    public List<Tag> getTag() {
        return section.getTag("TAG").getValues(DTag.TAG_CONVERTER);
    }

    public List<Stream> getStreams() {
        return Collections.emptyList();
    }

    public int getprogramId() {
        return section.getInteger("program_id");
    }

    public int getprogramNum() {
        return section.getInteger("program_num");
    }

    public int getnbStreams() {
        return section.getInteger("nb_streams");
    }

    public Float getstartTime() {
        return section.getFloat("start_time");
    }

    public Long getstartPts() {
        return section.getLong("start_pts");
    }

    public Float getendTime() {
        return section.getFloat("end_time");
    }

    public Long getendPts() {
        return section.getLong("end_pts");
    }

    public int getpmtPid() {
        return section.getInteger("pmt_pid");
    }

    public int getpcrPid() {
        return section.getInteger("pcr_pid");
    }
}
