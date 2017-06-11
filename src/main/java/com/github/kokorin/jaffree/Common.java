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

package com.github.kokorin.jaffree;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class Common<T extends Common> {
    private String format;
    private Long duration;
    private Long position;
    private Long positionEof;
    //-r[:stream_specifier] fps (input/output,per-stream)
    //-s[:stream_specifier] size (input/output,per-stream)

    //-ar[:stream_specifier] freq (input/output,per-stream)
    //-ac[:stream_specifier] channels (input/output,per-stream)
    //

    private final List<Codec> codecs = new ArrayList<>();
    private final List<Option> additionalOptions = new ArrayList<>();

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
     * @param unit time unit
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
     * @param positionEof position, relative to the EOF
     * @param unit time unit
     * @return this
     * @see #setPositionEof(long)
     */
    public T setPositionEof(long positionEof, TimeUnit unit) {
        this.positionEof = unit.toMillis(positionEof);
        return thisAsT();
    }

    public T addCodec(StreamSpecifier streamSpecifier, String codecName) {
        codecs.add(new Codec(streamSpecifier, codecName));
        return thisAsT();
    }

    /**
     * Add custom option.
     * Intended for options, that are not yet supported by jaffree
     *
     * @param option option to add
     * @return this
     */
    public T addOption(Option option) {
        additionalOptions.add(option);
        return thisAsT();
    }

    public abstract List<Option> buildOptions();

    protected List<Option> buildCommonOptions() {
        List<Option> result = new ArrayList<>();

        if (format != null) {
            result.add(new Option("-f", format));
        }

        if (duration != null) {
            result.add(new Option("-t", formatDuration(duration)));
        }

        if (position != null) {
            result.add(new Option("-ss", formatDuration(position)));
        }

        if (positionEof != null) {
            result.add(new Option("-sseof", formatDuration(positionEof)));
        }

        for (Codec codec : codecs) {
            String optionName = "-codec";
            if (codec.streamSpecifier != null) {
                optionName += ":" + codec.streamSpecifier.getValue();
            }

            result.add(new Option(optionName, codec.name));
        }

        result.addAll(additionalOptions);

        return result;
    }

    @SuppressWarnings("unchecked")
    private T thisAsT() {
        return (T) this;
    }

    private static class Codec {
        public StreamSpecifier streamSpecifier;
        public String name;

        public Codec(StreamSpecifier streamSpecifier, String name) {
            this.streamSpecifier = streamSpecifier;
            this.name = name;
        }
    }

    static String formatDuration(long durationMillis) {
        NumberFormat format = DecimalFormat.getNumberInstance(Locale.ROOT);
        format.setMaximumFractionDigits(3);
        return format.format(durationMillis * 0.001);
    }
}
