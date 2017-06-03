package com.github.kokorin.jaffree.cli;

public enum LogLevel {

    /**
     * Show nothing at all; be silent.
     */
    QUIET(-8),

    /**
     * Only show fatal errors which could lead the process to crash, such as an assertion failure.
     *
     * This is not currently used for anything.
     */
    PANIC(0),


    /**
     * Only show fatal errors.
     *
     * These are errors after which the process absolutely cannot continue.
     */
    FATAL(8),


    /**
     * Show all errors, including ones which can be recovered from.
     */
    ERROR(16),


    /**
     * Show all warnings and errors.
     *
     * Any message related to possibly incorrect or unexpected events will be shown.
     */
    WARNING(24),


    /**
     * Show informative messages during processing.
     *
     * This is in addition to warnings and errors. This is the default value.
     */
    INFO(32),


    /**
     * Same as info, except more verbose.
     */
    VERBOSE(40),


    /**
     * Show everything, including debugging information.
     */
    DEBUG(48),

    TRACE(56);

    private int code;

    LogLevel(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
