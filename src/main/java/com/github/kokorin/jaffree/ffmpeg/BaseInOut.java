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

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BaseInOut<T extends BaseInOut> {
    private String format;
    private Long duration;
    private Long position;
    private Long positionEof;


    //-ar[:stream_specifier] freq (input/output,per-stream)
    //-ac[:stream_specifier] channels (input/output,per-stream)
    //

    private final Map<String, Object> frameRates = new LinkedHashMap<>();
    private final Map<String, Object> frameSizes = new LinkedHashMap<>();
    private final Map<String, Object> codecs = new LinkedHashMap<>();
    private final Map<String, Object> pixelformats = new LinkedHashMap<>();
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
    public T setFrameRate(Number value) {
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
    public T setFrameRate(String streamSpecifier, Number value) {
        this.frameRates.put(streamSpecifier, value);
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
    public T setFrameSize(Number width, Number height) {
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
    public T setFrameSize(String streamSpecifier, Number width, Number height) {
        return setFrameSize(streamSpecifier, width + "x" + height);
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
     * @param resolution width + "x" + height
     * @return this
     */
    public T setFrameSize(String streamSpecifier, String resolution) {
        this.frameSizes.put(streamSpecifier, resolution);
        return thisAsT();
    }

    public T setCodec(StreamType type, String codecName) {
        return setCodec(type.code(), codecName);
    }

    public T setCodec(String streamSpecifier, String codecName) {
        codecs.put(streamSpecifier, codecName);
        return thisAsT();
    }

    public T setPixelFormat(String format) {
        return setPixelFormat(null, format);
    }

    public T setPixelFormat(String streamSpecifier, String value) {
        pixelformats.put(streamSpecifier, value);
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

    protected final List<String> buildCommonArguments() {
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

        result.addAll(toArguments("-r", frameRates));
        result.addAll(toArguments("-s", frameSizes));
        result.addAll(toArguments("-c", codecs));
        result.addAll(toArguments("-pix_fmt", pixelformats));

        result.addAll(additionalArguments);

        return result;
    }

    @SuppressWarnings("unchecked")
    protected final T thisAsT() {
        return (T) this;
    }

    static String formatDuration(long durationMillis) {
        return String.format("%d.%03d", durationMillis / 1000, Math.abs(durationMillis) % 1000);
    }

    protected static List<String> toArguments(String key, Map<String, Object> args) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Object> arg : args.entrySet()) {
            String specifier = arg.getKey();
            Object valueObj = arg.getValue();
            String value = valueObj != null ? valueObj.toString() : null;

            if (value == null) {
                continue;
            }

            if (specifier == null || specifier.isEmpty()) {
                result.add(key);
                result.add(value);
                continue;
            }

            result.add(key + ":" + specifier);
            result.add(value);
        }

        return result;
    }
}
