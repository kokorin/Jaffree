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

import com.github.kokorin.jaffree.StreamType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class UrlInOut<T extends UrlInOut> {
    private String format;
    private Long duration;
    private Long position;
    private Long positionEof;

    private StreamSpecifierWithValue frameRate;
    private StreamSpecifierWithValue frameSize;

    //-ar[:stream_specifier] freq (input/output,per-stream)
    //-ac[:stream_specifier] channels (input/output,per-stream)
    //

    private final List<StreamSpecifierWithValue> codecs = new ArrayList<>();
    private final List<String> additionalArguments = new ArrayList<>();

    /**
     * Force input or output file format. The format is normally auto detected for input files and
     * guessed from the file extension for output files, so this option is not needed in most cases.
     *
     * @param format format
     * @return this
     */
    public T setFormat(String format) {
        this.format = format;
        return thisAsT();
    }

    /**
     * When used as an input option, limit the duration of data read from the input file.
     * <p>
     * When used as an output option, stop writing the output after its duration reaches duration.
     *
     * @param durationMillis duration in milliseconds
     * @return this
     */
    public T setDuration(long durationMillis) {
        this.duration = durationMillis;
        return thisAsT();
    }

    /**
     * @param duration duration
     * @param timeUnit unit of duration
     * @return this
     * @see #setDuration(long)
     */
    public T setDuration(long duration, TimeUnit timeUnit) {
        this.duration = timeUnit.toMillis(duration);
        return thisAsT();
    }

    /**
     * When used as an input option, seeks in this input file to position.
     * <p>
     * Note that in most formats it is not possible to seek exactly, so ffmpeg will seek
     * to the closest seek point before position.
     * When transcoding and -accurate_seek is enabled (the default), this extra segment between
     * the seek point and position will be decoded and discarded.
     * When doing stream copy or when -noaccurate_seek is used, it will be preserved.
     * <p>
     * When used as an output option (before an output url), decodes but discards input until the timestamps reach position.
     *
     * @param positionMillis position in milliseconds.
     * @return this
     */
    public T setPosition(long positionMillis) {
        this.position = positionMillis;
        return thisAsT();
    }

    /**
     * @param position position.
     * @param unit     time unit
     * @return this
     * @see #setPosition(long)
     */
    public T setPosition(long position, TimeUnit unit) {
        this.position = unit.toMillis(position);
        return thisAsT();
    }

    /**
     * Like the {@link #setPosition(long)} (-ss) option but relative to the "end of file".
     * That is negative values are earlier in the file, 0 is at EOF.
     *
     * @param positionEofMillis position in milliseconds, relative to the EOF
     * @return this
     */
    public T setPositionEof(long positionEofMillis) {
        this.positionEof = positionEofMillis;
        return thisAsT();
    }

    /**
     * Like the {@link #setPositionEof(long)}  (-ss) option but relative to the "end of file".
     * That is negative values are earlier in the file, 0 is at EOF.
     *
     * @param positionEof position, relative to the EOF
     * @param unit        time unit
     * @return this
     * @see #setPositionEof(long)
     */
    public T setPositionEof(long positionEof, TimeUnit unit) {
        this.positionEof = unit.toMillis(positionEof);
        return thisAsT();
    }

    public T setCodec(StreamType type, String codecName) {
        return setCodec(type.code(), codecName);
    }

    public T setCodec(String streamSpecifier, String codecName) {
        codecs.add(new StreamSpecifierWithValue(streamSpecifier, codecName));
        return thisAsT();
    }

    /**
     * Set frame rate.
     * <p>
     * As an input option, ignore any timestamps stored in the file and instead generate timestamps assuming constant frame rate fps.
     * <p>
     * As an output option, duplicate or drop input frames to achieve constant output frame rate fps.
     *
     * @param value Hz value, fraction or abbreviation
     * @return this
     */
    public T setFrameRate(String value) {
        return setFrameRate(null, value);
    }

    /**
     * Set frame rate.
     * <p>
     * As an input option, ignore any timestamps stored in the file and instead generate timestamps assuming constant frame rate fps.
     * <p>
     * As an output option, duplicate or drop input frames to achieve constant output frame rate fps.
     *
     * @param streamSpecifier stream specifier
     * @param value           Hz value, fraction or abbreviation
     * @return this
     */
    public T setFrameRate(String streamSpecifier, String value) {
        this.frameRate = new StreamSpecifierWithValue(streamSpecifier, value);
        return thisAsT();
    }

    /**
     * Set frame size
     * <p>
     * As an input option, this is a shortcut for the video_size private option, recognized by some demuxers
     * for which the frame size is either not stored in the file or is configurable
     * <p>
     * As an output option, this inserts the scale video filter to the end of the corresponding filtergraph.
     *
     * @param width  frame width
     * @param height frame height
     * @return this
     */
    public T setFrameSize(String width, String height) {
        return setFrameSize(null, width, height);
    }

    /**
     * Set frame size
     * <p>
     * As an input option, this is a shortcut for the video_size private option, recognized by some demuxers
     * for which the frame size is either not stored in the file or is configurable
     * <p>
     * As an output option, this inserts the scale video filter to the end of the corresponding filtergraph.
     *
     * @param streamSpecifier stream specifier
     * @param width           frame width
     * @param height          frame height
     * @return this
     */
    public T setFrameSize(String streamSpecifier, String width, String height) {
        this.frameSize = new StreamSpecifierWithValue(streamSpecifier, width + "x" + height);
        return thisAsT();
    }

    /**
     * Add custom arguments.
     * Intended for cases, that are not yet supported by jaffree
     *
     * @param key   key to add
     * @param value value to add
     * @return this
     */
    public T addArguments(String key, String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return thisAsT();
    }

    /**
     * Add custom argument.
     * Intended for cases, that are not yet supported by jaffree
     *
     * @param argument argument to add
     * @return this
     */
    public T addArgument(String argument) {
        additionalArguments.add(argument);
        return thisAsT();
    }

    public abstract List<String> buildArguments();

    protected List<String> buildCommonArguments() {
        List<String> result = new ArrayList<>();

        if (format != null) {
            result.addAll(Arrays.asList("-f", format));
        }

        if (duration != null) {
            result.addAll(Arrays.asList("-t", formatDuration(duration)));
        }

        if (position != null) {
            result.addAll(Arrays.asList("-ss", formatDuration(position)));
        }

        if (positionEof != null) {
            result.addAll(Arrays.asList("-sseof", formatDuration(positionEof)));
        }

        if (frameRate != null) {
            result.addAll(frameRate.toArguments("-r"));
        }

        if (frameSize != null) {
            result.addAll(frameSize.toArguments("-s"));
        }

        for (StreamSpecifierWithValue codec : codecs) {
            result.addAll(codec.toArguments("-codec"));
        }

        result.addAll(additionalArguments);

        return result;
    }

    @SuppressWarnings("unchecked")
    private T thisAsT() {
        return (T) this;
    }

    static String formatDuration(long durationMillis) {
        return String.format("%d.%03d", durationMillis / 1000, Math.abs(durationMillis) % 1000);
    }

    protected static class StreamSpecifierWithValue {
        public final String streamSpecifier;
        public final String value;

        public StreamSpecifierWithValue(String streamSpecifier, String value) {
            this.streamSpecifier = streamSpecifier;
            this.value = value;
        }

        public List<String> toArguments(String key) {
            if (streamSpecifier == null) {
                return Arrays.asList(key, value);
            }

            return Arrays.asList(key + ":" + streamSpecifier, value);
        }
    }
}
