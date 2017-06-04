package com.github.kokorin.jaffree.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Output extends Common<Output> {
    private String url;
    private Long outputPosition;
    private Long sizeLimit;
    //-timestamp date (output)
    //-metadata[:metadata_specifier] key=value (output,per-metadata)
    //-disposition[:stream_specifier] value (output,per-stream)
    //-program [title=title:][program_num=program_num:]st=stream[:st=stream...] (output)
    //-target type (output)
    //-dframes number (output)
    //-frames[:stream_specifier] framecount (output,per-stream)
    //-qscale[:stream_specifier] q (output,per-stream)
    //-filter[:stream_specifier] filtergraph (output,per-stream)
    //-filter_script[:stream_specifier] filename (output,per-stream)
    //-pre[:stream_specifier] preset_name (output,per-stream)
    //-attach filename (output)
    //-vframes number (output)
    //-aspect[:stream_specifier] aspect (output,per-stream)
    //-vn (output)
    //-pass[:stream_specifier] n (output,per-stream)
    //-passlogfile[:stream_specifier] prefix (output,per-stream)
    //

    public Output setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Stop writing the output at outputPosition.
     * <p>
     * outputPosition (-to) and duration (-t) are mutually exclusive and duration has priority.
     *
     * @param positionMillis outputPosition in milliseconds
     * @return this
     * @see #setDuration(long)
     *
     * TODO do we need this method? It seems, that it has the same effect as Common#setDuration
     */
    public Output setOutputPosition(long positionMillis) {
        this.outputPosition = outputPosition;
        return this;
    }

    /**
     * Stop writing the output at outputPosition.
     * <p>
     * outputPosition (-to) and duration (-t) are mutually exclusive and duration has priority.
     *
     * @param position outputPosition
     * @param unit unit
     * @return this
     * @see #setDuration(long)
     *
     * TODO do we need this method? It seems, that it has the same effect as Common#setDuration
     */
    public Output setOutputPosition(long position, TimeUnit unit) {
        this.outputPosition = unit.toMillis(position);
        return this;
    }

    /**
     * Set the file size limit, expressed in bytes. No further chunk of bytes is written after the limit is exceeded.
     * The size of the output file is slightly more than the requested file size.
     * @param sizeLimitBytes size limit in bytes
     * @return this
     */
    public Output setSizeLimit(long sizeLimitBytes) {
        this.sizeLimit = sizeLimitBytes;
        return this;
    }

    /**
     * Set the file size limit. No further chunk of bytes is written after the limit is exceeded.
     * The size of the output file is slightly more than the requested file size.
     * @param sizeLimit size limit
     * @param unit size unit
     * @return this
     */
    public Output setSizeLimit(long sizeLimit, SizeUnit unit) {
        this.sizeLimit = sizeLimit * unit.multiplier();
        return this;
    }

    @Override
    public List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        if (outputPosition != null) {
            result.add(new Option("-to", formatDuration(outputPosition)));
        }

        if (sizeLimit != null) {
            result.add(new Option("-fs", sizeLimit.toString()));
        }

        result.addAll(buildCommonOptions());

        // must be the last option
        result.add(new Option(url));

        return result;
    }
}
