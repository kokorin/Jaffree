package com.github.kokorin.jaffree.cli;

import java.util.ArrayList;
import java.util.List;

public abstract class Common<T extends Common> {
    private String format;
    private final List<Codec> codecs = new ArrayList<>();
    private final List<Option> additionalOptions = new ArrayList<>();

    /**
     * Force input or output file format. The format is normally auto detected for input files and
     * guessed from the file extension for output files, so this option is not needed in most cases.
     * @param format format
     * @return this
     */
    public T setFormat(String format) {
        this.format = format;
        return thisAsT();
    }

    public T addCodec(StreamSpecifier streamSpecifier, String codecName) {
        codecs.add(new Codec(streamSpecifier, codecName));
        return thisAsT();
    }

    /**
     * Add custom option.
     * Intended for options, that are not yet supported by jaffree
     * @param option option to add
     * @return this
     */
    public Common addOption(Option option) {
        additionalOptions.add(option);
        return thisAsT();
    }

    public abstract List<Option> buildOptions();

    protected List<Option> buildCommonOptions() {
        List<Option> result = new ArrayList<>();

        if (format != null) {
            result.add(new Option("-f", format));
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
}
