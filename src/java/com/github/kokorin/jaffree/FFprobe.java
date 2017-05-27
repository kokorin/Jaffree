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

    private Boolean sections = false;
    private List<StreamSpecifier> selectStreams;
    private Boolean showData;
    private String showDataHash;
    private Boolean showError;
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

    /**
     * Print sections structure and section information, and exit.
     * <p>
     * The output is not meant to be parsed by a machine.
     *
     * @param sections
     * @return this
     */
    public FFprobe setSections(boolean sections) {
        this.sections = sections;
        return this;
    }

    /**
     * Select only the streams specified by stream_specifier.
     * <p>
     * This option affects only the options related to streams (e.g. show_streams, show_packets, etc.).
     *
     * @param selectStreams
     * @return this
     */
    public FFprobe setSelectStreams(List<StreamSpecifier> selectStreams) {
        this.selectStreams = selectStreams;
        return this;
    }

    /**
     * Show payload data, as a hexadecimal and ASCII dump.
     * <p>
     * Coupled with -show_packets, it will dump the packets’ data.
     * <p>
     * Coupled with -show_streams, it will dump the codec extradata.
     *
     * @param showData
     * @return this
     */
    public FFprobe setShowData(boolean showData) {
        this.showData = showData;
        return this;
    }

    /**
     * Show a hash of payload data, for packets with -show_packets and for codec extradata with -show_streams.
     *
     * @param showDataHash
     * @return this
     */
    public FFprobe setShowDataHash(String showDataHash) {
        this.showDataHash = showDataHash;
        return this;
    }

    /**
     * Show information about the error found when trying to probe the input.
     *
     * @param showError
     * @return this
     */
    public FFprobe setShowError(Boolean showError) {
        this.showError = showError;
        return this;
    }

    /**
     * Show information about the container format of the input multimedia stream.
     *
     * @param showFormat
     * @return this
     */
    public FFprobe setShowFormat(boolean showFormat) {
        this.showFormat = showFormat;
        return this;
    }

    /**
     * Like -show_format, but only prints the specified entry of the container format information, rather than all.
     * <p>
     * This option may be given more than once, then all specified entries will be shown.
     *
     * @param showFormatEntry
     * @return this
     * @deprecated This option is deprecated, use show_entries instead.
     */
    public FFprobe setShowFormatEntry(String showFormatEntry) {
        this.showFormatEntry = showFormatEntry;
        return this;
    }

    /**
     * Set list of entries to show.
     * <p>
     * Note that the order of specification of the local section entries is not honored in the output,
     * and the usual display order will be retained.
     *
     * @param showEntries
     * @return this
     */
    public FFprobe setShowEntries(String showEntries) {
        this.showEntries = showEntries;
        return this;
    }

    /**
     * Show information about each packet contained in the input multimedia stream.
     *
     * @param showPackets
     * @return this
     */
    public FFprobe setShowPackets(boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    /**
     * Show information about each frame and subtitle contained in the input multimedia stream.
     *
     * @param showFrames
     * @return this
     */
    public FFprobe setShowFrames(boolean showFrames) {
        this.showFrames = showFrames;
        return this;
    }

    /**
     * Show logging information from the decoder about each frame according to the value set in loglevel, (see -loglevel).
     * <p>
     * This option requires -show_frames.
     *
     * @param logLevel
     * @return this
     */
    public FFprobe setShowLog(LogLevel logLevel) {
        this.showLog = logLevel;
        return this;
    }

    /**
     * Show information about each media stream contained in the input multimedia stream.
     *
     * @param showStreams
     * @return this
     */
    public FFprobe setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    /**
     * Show information about programs and their streams contained in the input multimedia stream.
     *
     * @param showPrograms
     * @return this
     */
    public FFprobe setShowPrograms(boolean showPrograms) {
        this.showPrograms = showPrograms;
        return this;
    }

    /**
     * Show information about chapters stored in the format.
     *
     * @param showChapters
     * @return this
     */
    public FFprobe setShowChapters(boolean showChapters) {
        this.showChapters = showChapters;
        return this;
    }

    /**
     * Count the number of frames per stream and report it in the corresponding stream section.
     *
     * @param countFrames
     * @return this
     */
    public FFprobe setCountFrames(boolean countFrames) {
        this.countFrames = countFrames;
        return this;
    }

    /**
     * Count the number of packets per stream and report it in the corresponding stream section.
     *
     * @param countPackets
     * @return this
     */
    public FFprobe setCountPackets(boolean countPackets) {
        this.countPackets = countPackets;
        return this;
    }

    /**
     * Read only the specified intervals. read_intervals must be a sequence of interval specifications separated by ",".
     * <p>
     * ffprobe will seek to the interval starting point, and will continue reading from that.
     *
     * @param readIntervals
     * @return this
     */
    public FFprobe setReadIntervals(String readIntervals) {
        this.readIntervals = readIntervals;
        return this;
    }

    /**
     * Show private data, that is data depending on the format of the particular shown element.
     * <p>
     * This option is enabled by default, but you may need to disable it for specific uses,
     * for example when creating XSD-compliant XML output.
     *
     * @param showPrivateData
     * @return this
     */
    public FFprobe setShowPrivateData(boolean showPrivateData) {
        this.showPrivateData = showPrivateData;
        return this;
    }

    /**
     * Show information related to program version.
     *
     * @param showProgramVersion
     * @return this
     */
    public FFprobe setShowProgramVersion(boolean showProgramVersion) {
        this.showProgramVersion = showProgramVersion;
        return this;
    }

    /**
     * Show information related to library versions.
     *
     * @param showLibraryVersions
     * @return this
     */
    public FFprobe setShowLibraryVersions(boolean showLibraryVersions) {
        this.showLibraryVersions = showLibraryVersions;
        return this;
    }

    /**
     * Show information related to program and library versions.
     * <p>
     * This is the equivalent of setting both -show_program_version and -show_library_versions
     *
     * @param showVersions
     * @return
     */
    public FFprobe setShowVersions(boolean showVersions) {
        this.showVersions = showVersions;
        return this;
    }

    /**
     * Show information about all pixel formats supported by FFmpeg.
     *
     * @param showPixelFormats
     * @return this
     */
    public FFprobe setShowPixelFormats(boolean showPixelFormats) {
        this.showPixelFormats = showPixelFormats;
        return this;
    }

    public FFprobe setInput(String input) {
        this.input = input;
        return this;
    }

    public FFprobeType execute() {
        List<Option> command = buildCommand();
        return null;
    }

    protected List<Option> buildCommand() {
        List<Option> result = new ArrayList<>();

        if (xsdCompliant != null) {
            result.add(new Option("-xsd_compliant", xsdCompliant.toString()));
        }
        if (bitExact != null) {
            result.add(new Option("-bitexact", bitExact.toString()));
        }
        if (sections != null) {
            result.add(new Option("-sections", sections.toString()));
        }
        if (selectStreams != null) {
            result.add(new Option("-select_streams", selectStreams.toString()));
        }
        if (showData != null) {
            result.add(new Option("-show_data", showData.toString()));
        }
        if (showDataHash != null) {
            result.add(new Option("-show_data_hash", showDataHash));
        }
        if (showError != null) {
            result.add(new Option("-show_error", showError.toString()));
        }
        if (showFormat != null) {
            result.add(new Option("-show_format", showFormat.toString()));
        }
        if (showFormatEntry != null) {
            result.add(new Option("-show_format_entry", showFormatEntry));
        }
        if (showEntries != null) {
            result.add(new Option("-show_entries", showEntries));
        }
        if (showPackets != null) {
            result.add(new Option("-show_packets", showPackets.toString()));
        }
        if (showFrames != null) {
            result.add(new Option("-show_frames", showFrames.toString()));
        }
        if (showLog != null) {
            result.add(new Option("-show_log", showLog.toString()));
        }
        if (showStreams != null) {
            result.add(new Option("-show_streams", showStreams.toString()));
        }
        if (showPrograms != null) {
            result.add(new Option("-show_programs", showPrograms.toString()));
        }
        if (showChapters != null) {
            result.add(new Option("-show_chapters", showChapters.toString()));
        }
        if (countFrames != null) {
            result.add(new Option("-count_frames", countFrames.toString()));
        }
        if (countPackets != null) {
            result.add(new Option("-count_packets", countPackets.toString()));
        }
        if (readIntervals != null) {
            result.add(new Option("-read_intervals", readIntervals));
        }
        if (showPrivateData != null) {
            result.add(new Option("-show_private_data", showPrivateData.toString()));
        }
        if (showProgramVersion != null) {
            result.add(new Option("-show_program_version", showProgramVersion.toString()));
        }
        if (showLibraryVersions != null) {
            result.add(new Option("-show_library_versions", showLibraryVersions.toString()));
        }
        if (showVersions != null) {
            result.add(new Option("-show_versions", showVersions.toString()));
        }
        if (showPixelFormats != null) {
            result.add(new Option("-show_pixel_formats", showPixelFormats.toString()));
        }
        if (input != null) {
            result.add(new Option("-i", input));
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
