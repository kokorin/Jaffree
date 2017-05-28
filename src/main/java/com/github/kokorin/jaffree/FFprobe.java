package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.cli.LogLevel;
import com.github.kokorin.jaffree.cli.Option;
import com.github.kokorin.jaffree.cli.StreamSpecifier;
import com.github.kokorin.jaffree.ffprobe.xml.FFprobeType;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FFprobe {
    private final Path executablePath;

    //This final options are required for well-formatted xml output
    private final String printFormat = "xml=\"x=1:q=1\"";
    private final boolean bitExact = true;

    private boolean sections = false;
    private List<StreamSpecifier> selectStreams;
    private boolean showData = false;
    private String showDataHash;
    private boolean showError = false;
    private boolean showFormat = false;
    private String showFormatEntry;
    //TODO extract type
    private String showEntries;
    private boolean showPackets = false;
    private boolean showFrames;
    private LogLevel showLog;
    private boolean showStreams = true;
    private boolean showPrograms = false;
    private boolean showChapters;
    private boolean countFrames;
    private boolean countPackets;
    //TODO extract type
    private String readIntervals;

    private boolean showPrivateData;
    private boolean showProgramVersion;
    private boolean showLibraryVersions;
    private boolean showVersions;
    private boolean showPixelFormats;
    private Path inputPath;

    protected FFprobe(Path executablePath) {
        this.executablePath = executablePath;
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
     * @param algorithm
     * @return this
     */
    public FFprobe setShowDataHash(String algorithm) {
        this.showDataHash = algorithm;
        return this;
    }

    /**
     * Show information about the error found when trying to probe the inputPath.
     *
     * @param showError
     * @return this
     */
    public FFprobe setShowError(boolean showError) {
        this.showError = showError;
        return this;
    }

    /**
     * Show information about the container format of the inputPath multimedia stream.
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
     * Show information about each packet contained in the inputPath multimedia stream.
     *
     * @param showPackets
     * @return this
     */
    public FFprobe setShowPackets(boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    /**
     * Show information about each frame and subtitle contained in the inputPath multimedia stream.
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
     * Show information about each media stream contained in the inputPath multimedia stream.
     *
     * @param showStreams
     * @return this
     */
    public FFprobe setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    /**
     * Show information about programs and their streams contained in the inputPath multimedia stream.
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

    public FFprobe setInputPath(Path inputPath) {
        this.inputPath = inputPath;
        return this;
    }

    public FFprobeType execute() {
        List<Option> options = buildOptions();

        List<String> command = new ArrayList<>();

        command.add(executablePath.toString());

        for (Option option : options) {
            command.add(option.getName());
            if (option.getValue() != null) {
                command.add(option.getValue());
            }
        }

        Process process;
        try {
            process = new ProcessBuilder(command).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        }

        FFprobeType result = null;
        Thread errorThread = null;
        try (InputStream stdOut = process.getInputStream();
             InputStream stdErr = process.getErrorStream()) {
            errorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String processError = "";
                    try {
                        List<String> lines = IOUtils.readLines(stdErr, StandardCharsets.UTF_8);
                        for (String line : lines) {
                            processError += line + "\n";
                        }
                        System.out.println(processError);
                        System.out.println("---------------");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            errorThread.start();

            result = JAXB.unmarshal(stdOut, FFprobeType.class);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (errorThread != null) {
                try {
                    errorThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    protected List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        if (bitExact) {
            result.add(new Option("-bitexact"));
        }

        if (printFormat != null) {
            result.add(new Option("-print_format", printFormat));
        }

        //result.add(new Option("-hide_banner"));

        if (sections) {
            result.add(new Option("-sections"));
        }
        if (selectStreams != null) {
            result.add(new Option("-select_streams", selectStreams.toString()));
        }
        if (showData) {
            result.add(new Option("-show_data"));
        }
        if (showDataHash != null) {
            result.add(new Option("-show_data_hash", showDataHash));
        }
        if (showError) {
            result.add(new Option("-show_error"));
        }
        if (showFormat) {
            result.add(new Option("-show_format"));
        }
        if (showFormatEntry != null) {
            result.add(new Option("-show_format_entry", showFormatEntry));
        }
        if (showEntries != null) {
            result.add(new Option("-show_entries", showEntries));
        }
        if (showPackets) {
            result.add(new Option("-show_packets"));
        }
        if (showFrames) {
            result.add(new Option("-show_frames"));
        }
        if (showLog != null) {
            result.add(new Option("-show_log", showLog.value()));
        }
        if (showStreams) {
            result.add(new Option("-show_streams"));
        }
        if (showPrograms) {
            result.add(new Option("-show_programs"));
        }
        if (showChapters) {
            result.add(new Option("-show_chapters"));
        }
        if (countFrames) {
            result.add(new Option("-count_frames"));
        }
        if (countPackets) {
            result.add(new Option("-count_packets"));
        }
        if (readIntervals != null) {
            result.add(new Option("-read_intervals", readIntervals));
        }
        if (showPrivateData) {
            result.add(new Option("-show_private_data"));
        } else {
            result.add(new Option("-noprivate"));
        }
        if (showProgramVersion) {
            result.add(new Option("-show_program_version"));
        }
        if (showLibraryVersions) {
            result.add(new Option("-show_library_versions"));
        }
        if (showVersions) {
            result.add(new Option("-show_versions"));
        }
        if (showPixelFormats) {
            result.add(new Option("-show_pixel_formats"));
        }

        Objects.requireNonNull(inputPath, "Input file not specified");
        result.add(new Option("-i", inputPath.toString()));

        return result;
    }

    public static FFprobe atPath(Path pathToDir) {
        String os = System.getProperty("os.name");
        if (os == null) {
            throw new RuntimeException("Failed to detect OS");
        }

        Path executable;
        if (os.toLowerCase().contains("win")) {
            executable = pathToDir.resolve("ffprobe.exe");
        } else {
            executable = pathToDir.resolve("ffprobe");
        }

        return new FFprobe(executable);
    }

}
