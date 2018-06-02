/*
 *    Copyright  2017 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.SizeUnit;
import com.github.kokorin.jaffree.StreamType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BaseOutput<T extends BaseInOut & Output> extends BaseInOut<T> implements Output {
    private String output;
    private Long outputPosition;
    private Long sizeLimit;
    private final Set<StreamType> disabledStreams = new LinkedHashSet<>();
    private final List<Map> maps = new ArrayList<>();

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
    //-rc_override[:stream_specifier] override (output,per-stream)

    //-aframes number (output)
    //-aq q (output)
    //-an (output)
    //-sample_fmt[:stream_specifier] sample_fmt (output,per-stream)


    protected T setOutput(String output) {
        this.output = output;
        return thisAsT();
    }

    /**
     * Stop writing the output at outputPosition.
     * <p>
     * outputPosition (-to) and duration (-t) are mutually exclusive and duration has priority.
     *
     * @param positionMillis outputPosition in milliseconds
     * @return this
     * @see #setDuration(long)
     */
    public T setOutputPosition(long positionMillis) {
        this.outputPosition = outputPosition;
        return thisAsT();
    }

    /**
     * Stop writing the output at outputPosition.
     * <p>
     * outputPosition (-to) and duration (-t) are mutually exclusive and duration has priority.
     *
     * @param position outputPosition
     * @param unit     unit
     * @return this
     * @see #setDuration(long)
     */
    public T setOutputPosition(long position, TimeUnit unit) {
        this.outputPosition = unit.toMillis(position);
        return thisAsT();
    }

    /**
     * Set the file size limit, expressed in bytes. No further chunk of bytes is written after the limit is exceeded.
     * The size of the output file is slightly more than the requested file size.
     *
     * @param sizeLimitBytes size limit in bytes
     * @return this
     */
    public T setSizeLimit(long sizeLimitBytes) {
        this.sizeLimit = sizeLimitBytes;
        return thisAsT();
    }

    /**
     * Set the file size limit. No further chunk of bytes is written after the limit is exceeded.
     * The size of the output file is slightly more than the requested file size.
     *
     * @param sizeLimit size limit
     * @param unit      size unit
     * @return this
     */
    public T setSizeLimit(long sizeLimit, SizeUnit unit) {
        this.sizeLimit = unit.toBytes(sizeLimit);
        return thisAsT();
    }

    /**
     * Sets special "copy" codec for all streams
     * @return this
     */
    public T copyAllCodecs() {
        return copyCodec((String)null);
    }

    /**
     * Sets special "copy" codec for specified streams
     * @return this
     */
    public T copyCodec(String streamSpecifier) {
        return setCodec(streamSpecifier, "copy");
    }

    public T copyCodec(StreamType streamType) {
        return setCodec(streamType, "copy");
    }


    public T disableStream(StreamType type) {
        disabledStreams.add(type);
        switch (type) {
            case VIDEO:
                setCodec(StreamType.VIDEO, null);
                setPixelFormat(null);
                break;
            case AUDIO:
                setCodec(StreamType.AUDIO, null);
                break;
        }

        return thisAsT();
    }

    /**
     * Maps all streams from the input file.
     * <p>
     * Each input stream is identified by the input file index input_file_id. Index starts at 0.
     *
     * @param inputFileIndex  index of input file
     * @return this
     */
    public T addMap(int inputFileIndex) {
        this.maps.add(new MapDefault(false, inputFileIndex, null, false));
        return thisAsT();
    }

    /**
     * Designate one or more input streams as a source for the output file.
     * <p>
     * Each input stream is identified by the input file index input_file_id and the input stream index
     * input_stream_id within the input file. Both indices start at 0.
     *
     * @param inputFileIndex  index of input file
     * @param streamType specifier for stream(s) in input file
     * @return this
     */
    public T addMap(int inputFileIndex, StreamType streamType) {
        this.maps.add(new MapDefault(false, inputFileIndex, streamType.code(), false));
        return thisAsT();
    }

    /**
     * Designate one or more input streams as a source for the output file.
     * <p>
     * Each input stream is identified by the input file index input_file_id and the input stream index
     * input_stream_id within the input file. Both indices start at 0.
     *
     * @param inputFileIndex  index of input file
     * @param streamSpecifier specifier for stream(s) in input file
     * @return this
     */
    public T addMap(int inputFileIndex, String streamSpecifier) {
        this.maps.add(new MapDefault(false, inputFileIndex, streamSpecifier, false));
        return thisAsT();
    }

    /**
     * An alternative [linklabel] form will map outputs from complex filter graphs (see the -filter_complex option)
     * to the output file. linklabel must correspond to a defined output link label in the graph.
     *
     * @param linkLabel label in complex filter
     * @return this
     */
    public T addMap(String linkLabel) {
        this.maps.add(new MapLabel(linkLabel));
        return thisAsT();
    }

    @Override
    public final List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (outputPosition != null) {
            result.addAll(Arrays.asList("-to", formatDuration(outputPosition)));
        }

        if (sizeLimit != null) {
            result.addAll(Arrays.asList("-fs", sizeLimit.toString()));
        }

        for (StreamType disabledStream : disabledStreams) {
            if (disabledStream == null) {
                continue;
            }
            addArgument("-" + disabledStream.code() + "n");
        }

        result.addAll(buildCommonArguments());

        for (Map map : maps) {
            result.addAll(Arrays.asList("-map", map.toValue()));
        }

        if (output == null) {
            throw new IllegalArgumentException("Output must be specified");
        }
        // must be the last option
        result.add(output);

        return result;
    }

    private static interface Map {
        String toValue();
    }

    private static class MapDefault implements Map {
        public boolean negative;
        public int inputFileId;
        public String streamSpecifier;
        public boolean optional;

        public MapDefault(boolean negative, int inputFileId, String streamSpecifier, boolean optional) {
            this.negative = negative;
            this.inputFileId = inputFileId;
            this.streamSpecifier = streamSpecifier;
            this.optional = optional;
        }

        @Override
        public String toValue() {
            StringBuilder result = new StringBuilder();

            if (negative) {
                result.append("-");
            }
            result.append(inputFileId);

            if (streamSpecifier != null) {
                result.append(":")
                        .append(streamSpecifier);
            }

            if (optional) {
                result.append("?");
            }

            return result.toString();
        }
    }

    private static class MapLabel implements Map {
        public String linkLabel;

        public MapLabel(String linkLabel) {
            this.linkLabel = linkLabel;
        }

        @Override
        public String toValue() {
            return "[" + linkLabel + "]";
        }
    }
}
