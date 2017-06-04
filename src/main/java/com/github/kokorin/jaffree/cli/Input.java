package com.github.kokorin.jaffree.cli;

import java.util.ArrayList;
import java.util.List;

public class Input extends Common<Input> {
    private String url;
    private Integer streamLoop;
    //-itsoffset offset (input)
    //-dump_attachment[:stream_specifier] filename (input,per-stream)

    /**
     * @param url input file url
     * @return this
     */
    public Input setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set number of times input stream shall be looped. Loop 0 means no loop, loop -1 means infinite loop.
     * @param streamLoop
     * @return this
     */
    public Input setStreamLoop(Integer streamLoop) {
        this.streamLoop = streamLoop;
        return this;
    }

    @Override
    public List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        if (streamLoop != null) {
            result.add(new Option("-stream_loop", streamLoop.toString()));
        }

        result.addAll(buildCommonOptions());

        // must be the last option
        if (url != null) {
            result.add(new Option("-i", url));
        }

        return result;
    }
}
