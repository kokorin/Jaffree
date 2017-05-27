package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.cli.LogLevel;
import com.github.kokorin.jaffree.cli.StreamSpecifier;
import com.github.kokorin.jaffree.ffprobe.xml.FFprobeType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FFprobe {
    private final Path path;

    //This final options are required for well-formatted xml output
    private final String outputFormat = "xml";
    private final Boolean xsdCompliant = true;
    private final Boolean bitExact = true;

    private Boolean showSections;
    private List<StreamSpecifier> selectStreams;
    private Boolean showData;
    private String showDataHash;
    private Boolean showFormat;
    private String showFormatEntry;
    //TODO extract type
    private String showEntries;
    private Boolean showPackets;
    private Boolean showFrames;
    private LogLevel showLog;
    private Boolean showStreams;
    private Boolean showPrograms;
    private Boolean showChapters;
    private Boolean countFrames;
    private Boolean countPackets;
    //TODO extract type
    private String readIntervals;

    private Boolean showPrivateData;
    private Boolean showProgramVersion;
    private Boolean showLibraryVersions;
    private Boolean showVersions;
    private Boolean showPixelFormats;
    private String input;

    protected FFprobe(Path path) {
        this.path = path;
    }

    public FFprobe setShowSections(boolean showSections) {
        this.showSections = showSections;
        return this;
    }

    public FFprobe setSelectStreams(List<StreamSpecifier> selectStreams) {
        this.selectStreams = selectStreams;
        return this;
    }

    public FFprobe setShowData(boolean showData) {
        this.showData = showData;
        return this;
    }

    public FFprobe setShowDataHash(String showDataHash) {
        this.showDataHash = showDataHash;
        return this;
    }

    public FFprobe setShowFormat(boolean showFormat) {
        this.showFormat = showFormat;
        return this;
    }

    public FFprobe setShowFormatEntry(String showFormatEntry) {
        this.showFormatEntry = showFormatEntry;
        return this;
    }

    public FFprobe setShowEntries(String showEntries) {
        this.showEntries = showEntries;
        return this;
    }

    public FFprobe setShowPackets(boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    public FFprobe setShowFrames(boolean showFrames) {
        this.showFrames = showFrames;
        return this;
    }

    public FFprobe setShowLog(LogLevel showLog) {
        this.showLog = showLog;
        return this;
    }

    public FFprobe setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    public FFprobe setShowPrograms(boolean showPrograms) {
        this.showPrograms = showPrograms;
        return this;
    }

    public FFprobe setShowChapters(boolean showChapters) {
        this.showChapters = showChapters;
        return this;
    }

    public FFprobe setCountFrames(boolean countFrames) {
        this.countFrames = countFrames;
        return this;
    }

    public FFprobe setCountPackets(boolean countPackets) {
        this.countPackets = countPackets;
        return this;
    }

    public FFprobe setReadIntervals(String readIntervals) {
        this.readIntervals = readIntervals;
        return this;
    }

    public FFprobe setShowPrivateData(boolean showPrivateData) {
        this.showPrivateData = showPrivateData;
        return this;
    }

    public FFprobe setShowProgramVersion(boolean showProgramVersion) {
        this.showProgramVersion = showProgramVersion;
        return this;
    }

    public FFprobe setInput(String input) {
        this.input = input;
        return this;
    }

    public FFprobe setShowLibraryVersions(boolean showLibraryVersions) {
        this.showLibraryVersions = showLibraryVersions;
        return this;
    }

    public FFprobe setShowVersions(boolean showVersions) {
        this.showVersions = showVersions;
        return this;
    }

    public FFprobe setShowPixelFormats(boolean showPixelFormats) {
        this.showPixelFormats = showPixelFormats;
        return this;
    }

    public FFprobeType execute() {
        List<Option> command = buildCommand();
        return null;
    }

    protected List<Option> buildCommand() {
        List<Option> result = new ArrayList<>();

        if (xsdCompliant != null) {
            result.add(new Option("xsdCompliant", xsdCompliant.toString()));
        }
        if (bitExact != null) {
            result.add(new Option("bitExact", bitExact.toString()));
        }
        if (showSections != null) {
            result.add(new Option("showSections", showSections.toString()));
        }
        if (selectStreams != null) {
            result.add(new Option("selectStreams", selectStreams.toString()));
        }
        if (showData != null) {
            result.add(new Option("showData", showData.toString()));
        }
        if (showDataHash != null) {
            result.add(new Option("showDataHash", showDataHash));
        }
        if (showFormat != null) {
            result.add(new Option("showFormat", showFormat.toString()));
        }
        if (showFormatEntry != null) {
            result.add(new Option("showFormatEntry", showFormatEntry));
        }
        if (showEntries != null) {
            result.add(new Option("showEntries", showEntries));
        }
        if (showPackets != null) {
            result.add(new Option("showPackets", showPackets.toString()));
        }
        if (showFrames != null) {
            result.add(new Option("showFrames", showFrames.toString()));
        }
        if (showLog != null) {
            result.add(new Option("showLog", showLog.toString()));
        }
        if (showStreams != null) {
            result.add(new Option("showStreams", showStreams.toString()));
        }
        if (showPrograms != null) {
            result.add(new Option("showPrograms", showPrograms.toString()));
        }
        if (showChapters != null) {
            result.add(new Option("showChapters", showChapters.toString()));
        }
        if (countFrames != null) {
            result.add(new Option("countFrames", countFrames.toString()));
        }
        if (countPackets != null) {
            result.add(new Option("countPackets", countPackets.toString()));
        }
        if (readIntervals != null) {
            result.add(new Option("readIntervals", readIntervals));
        }
        if (showPrivateData != null) {
            result.add(new Option("showPrivateData", showPrivateData.toString()));
        }
        if (showProgramVersion != null) {
            result.add(new Option("showProgramVersion", showProgramVersion.toString()));
        }
        if (showLibraryVersions != null) {
            result.add(new Option("showLibraryVersions", showLibraryVersions.toString()));
        }
        if (showVersions != null) {
            result.add(new Option("showVersions", showVersions.toString()));
        }
        if (showPixelFormats != null) {
            result.add(new Option("showPixelFormats", showPixelFormats.toString()));
        }
        if (input != null) {
            result.add(new Option("input", input));
        }

        return result;
    }

    public static FFprobe atPath(Path pathToDir) {
        return new FFprobe(pathToDir);
    }

    public static class Option {
        public String name;
        public String value;

        public Option(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
