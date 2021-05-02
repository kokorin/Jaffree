/*
 *    Copyright 2017-2021 Denis Kokorin
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
import com.github.kokorin.jaffree.process.ProcessHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base class which handles common arguments for both ffmpeg input &amp; output.
 *
 * @param <T> self
 */
public abstract class BaseInOut<T extends BaseInOut<T>> {
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
    private final Map<String, Object> pixelFormats = new LinkedHashMap<>();
    private final List<String> additionalArguments = new ArrayList<>();

    /**
     * Force input or output file format. The format is normally auto detected
     * for input files and guessed from the file extension for output files,
     * so this option is not needed in most cases.
     *
     * @param format format
     * @return this
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setFormat(final String format) {
        this.format = format;
        return thisAsT();
    }

    /**
     * When used as an input option, limit the duration of data read from the
     * input file.
     * <p>
     * When used as an output option, stop writing the output after its
     * duration reaches duration.
     *
     * @param durationMillis duration in milliseconds
     * @return this
     */
    public T setDuration(final long durationMillis) {
        this.duration = durationMillis;
        return thisAsT();
    }

    /**
     * When used as an input option, limit the duration of data read from
     * the input file.
     * <p>
     * When used as an output option, stop writing the output after
     * its duration reaches duration.
     *
     * @param duration duration
     * @param timeUnit unit of duration
     * @return this
     * @see #setDuration(long)
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setDuration(final Number duration, final TimeUnit timeUnit) {
        long millis = (long) (duration.doubleValue() * timeUnit.toMillis(1));
        return setDuration(millis);
    }

    /**
     * When used as an input option, seeks in this input file to position.
     * <p>
     * Note that in most formats it is not possible to seek exactly, so ffmpeg
     * will seek to the closest seek point before position.
     * When transcoding and -accurate_seek is enabled (the default), this extra
     * segment between the seek point and position will be decoded and
     * discarded.
     * <p>
     * When doing stream copy or when -noaccurate_seek is used,
     * it will be preserved.
     * <p>
     * When used as an output option (before an output url), decodes but
     * discards input until the timestamps reach position.
     *
     * @param positionMillis position in milliseconds.
     * @return this
     */
    public T setPosition(final long positionMillis) {
        this.position = positionMillis;
        return thisAsT();
    }

    /**
     * When used as an input option, seeks in this input file to position.
     * <p>
     * Note that in most formats it is not possible to seek exactly, so ffmpeg
     * will seek to the closest seek point before position.
     * When transcoding and -accurate_seek is enabled (the default), this extra
     * segment between the seek point and position will be decoded and
     * discarded.
     * <p>
     * When doing stream copy or when -noaccurate_seek is used,
     * it will be preserved.
     * <p>
     * When used as an output option (before an output url), decodes but
     * discards input until the timestamps reach position.
     *
     * @param position position.
     * @param unit     time unit
     * @return this
     * @see #setPosition(long)
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setPosition(final Number position, final TimeUnit unit) {
        long millis = (long) (position.doubleValue() * unit.toMillis(1));
        return setPosition(millis);
    }

    /**
     * Like the {@link #setPosition(long)} (-ss) option but relative to
     * the "end of file".
     * That is negative values are earlier in the file, 0 is at EOF.
     *
     * @param positionEofMillis position in milliseconds, relative to the EOF
     * @return this
     */
    public T setPositionEof(final long positionEofMillis) {
        this.positionEof = positionEofMillis;
        return thisAsT();
    }

    /**
     * Like the {@link #setPosition(Number, TimeUnit)}  (-ss) option but relative
     * to the "end of file".
     * That is negative values are earlier in the file, 0 is at EOF.
     *
     * @param positionEof position, relative to the EOF
     * @param unit        time unit
     * @return this
     * @see #setPositionEof(long)
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setPositionEof(final Number positionEof, final TimeUnit unit) {
        long millis = (long) (positionEof.doubleValue() * unit.toMillis(1));
        return setPositionEof(millis);
    }

    /**
     * Set frame rate.
     * <p>
     * As an input option, ignore any timestamps stored in the file and instead
     * generate timestamps assuming constant frame rate fps.
     * <p>
     * As an output option, duplicate or drop input frames to achieve
     * constant output frame rate fps.
     *
     * @param frameRate Hz value, fraction or abbreviation
     * @return this
     */
    public T setFrameRate(final Number frameRate) {
        return setFrameRate(null, frameRate);
    }

    /**
     * Set frame rate.
     * <p>
     * As an input option, ignore any timestamps stored in the file and instead
     * generate timestamps assuming constant frame rate fps.
     * <p>
     * As an output option, duplicate or drop input frames to achieve
     * constant output frame rate fps.
     *
     * @param streamSpecifier stream specifier
     * @param frameRate       Hz value, fraction or abbreviation
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public T setFrameRate(final String streamSpecifier, final Number frameRate) {
        this.frameRates.put(streamSpecifier, frameRate);
        return thisAsT();
    }

    /**
     * Set frame size
     * <p>
     * As an input option, this is a shortcut for the video_size private option,
     * recognized by some demuxers for which the frame size is either not stored
     * in the file or is configurable
     * <p>
     * As an output option, this inserts the scale video filter to the end of
     * the corresponding filtergraph.
     *
     * @param width  frame width
     * @param height frame height
     * @return this
     */
    public T setFrameSize(final Number width, final Number height) {
        return setFrameSize(null, width, height);
    }

    /**
     * Set frame size
     * <p>
     * As an input option, this is a shortcut for the video_size private option,
     * recognized by some demuxers for which the frame size is either not stored
     * in the file or is configurable
     * <p>
     * As an output option, this inserts the scale video filter to the end of
     * the corresponding filtergraph.
     *
     * @param streamSpecifier stream specifier
     * @param width           frame width
     * @param height          frame height
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public T setFrameSize(final String streamSpecifier, final Number width, final Number height) {
        return setFrameSize(streamSpecifier, width + "x" + height);
    }

    /**
     * Set frame size
     * <p>
     * As an input option, this is a shortcut for the video_size private option,
     * recognized by some demuxers for which the frame size is either not stored
     * in the file or is configurable
     * <p>
     * As an output option, this inserts the scale video filter to the end of
     * the corresponding filtergraph.
     *
     * @param streamSpecifier stream specifier
     * @param resolution      width + "x" + height
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public T setFrameSize(final String streamSpecifier, final String resolution) {
        this.frameSizes.put(streamSpecifier, resolution);
        return thisAsT();
    }

    /**
     * Select an encoder (when used before an output file) or a decoder
     * (when used before an input file) for one or more streams.
     * <p>
     * codec is the name of a decoder/encoder or a special value <b>copy</b>
     * (output only) to indicate that the stream is not to be re-encoded.
     *
     * @param streamType stream type
     * @param codec      codec name
     * @return this
     */
    public T setCodec(final StreamType streamType, final String codec) {
        return setCodec(streamType.code(), codec);
    }

    /**
     * Select an encoder (when used before an output file) or a decoder
     * (when used before an input file) for one or more streams.
     * <p>
     * codec is the name of a decoder/encoder or a special value <b>copy</b>
     * (output only) to indicate that the stream is not to be re-encoded.
     *
     * @param streamSpecifier stream specifier
     * @param codec           codec name
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public T setCodec(final String streamSpecifier, final String codec) {
        codecs.put(streamSpecifier, codec);
        return thisAsT();
    }

    /**
     * Set pixel format.
     * <p>
     * If the selected pixel format can not be selected, ffmpeg will print
     * a warning and select the best pixel format supported by the encoder.
     * <p>
     * If pix_fmt is prefixed by a +, ffmpeg will exit with an error if
     * the requested pixel format can not be selected, and automatic
     * conversions inside filtergraphs are disabled.
     * <p>
     * If pix_fmt is a single +, ffmpeg selects the same pixel format as
     * the input (or graph output) and automatic conversions are disabled.
     *
     * @param pixelFormat pixel format
     * @return this
     */
    public T setPixelFormat(final String pixelFormat) {
        return setPixelFormat(null, pixelFormat);
    }

    /**
     * Set pixel format.
     * <p>
     * If the selected pixel format can not be selected, ffmpeg will print
     * a warning and select the best pixel format supported by the encoder.
     * <p>
     * If pix_fmt is prefixed by a +, ffmpeg will exit with an error if
     * the requested pixel format can not be selected, and automatic
     * conversions inside filtergraphs are disabled.
     * <p>
     * If pix_fmt is a single +, ffmpeg selects the same pixel format as
     * the input (or graph output) and automatic conversions are disabled.
     *
     * @param streamSpecifier stream specifier
     * @param pixelFormat     pixel format
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public T setPixelFormat(final String streamSpecifier, final String pixelFormat) {
        pixelFormats.put(streamSpecifier, pixelFormat);
        return thisAsT();
    }

    /**
     * Add custom input/output specific arguments.
     * <p>
     * <b>Note:</b> if value contains spaces it <b>should not</b> be wrapped
     * with quotes. Also spaces <b>should not</b> be escaped with backslash
     *
     * @param key   key to add
     * @param value value to add
     * @return this
     */
    public T addArguments(final String key, final String value) {
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
    public T addArgument(final String argument) {
        additionalArguments.add(argument);
        return thisAsT();
    }

    /**
     * Helper {@link ProcessHelper} which should be ran in dedicated thread. Default implementation
     * always returns null.
     *
     * @return ProcessHelper, or null if no helper thread is needed
     * @see Input#helperThread()
     * @see Output#helperThread()
     */
    public ProcessHelper helperThread() {
        return null;
    }


    /**
     * Build a list of command line arguments that are common for ffmpeg input &amp; output.
     *
     * @return list of command line arguments
     */
    protected List<String> buildArguments() {
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
        result.addAll(toArguments("-pix_fmt", pixelFormats));

        return result;
    }

    protected final List<String> getAdditionalArguments() {
        return Collections.unmodifiableList(additionalArguments);
    }

    @SuppressWarnings("unchecked")
    protected final T thisAsT() {
        return (T) this;
    }

    static String formatDuration(final long durationMillis) {
        long divider = TimeUnit.SECONDS.toMillis(1);
        long seconds = durationMillis / divider;
        long millis = Math.abs(durationMillis) % divider;
        return String.format("%d.%03d", seconds, millis);
    }

    protected static List<String> toArguments(final String key, final Map<String, Object> args) {
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
