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

/**
 * FFmpeg &amp; FFprobe log level.
 */
public enum LogLevel {

    /**
     * Show nothing at all; be silent.
     */
    QUIET(-8),

    /**
     * Only show fatal errors which could lead the process to crash, such as an assertion failure.
     * <p>
     * This is not currently used for anything.
     */
    PANIC(0),


    /**
     * Only show fatal errors.
     * <p>
     * These are errors after which the process absolutely cannot continue.
     */
    FATAL(8),


    /**
     * Show all errors, including ones which can be recovered from.
     */
    ERROR(16),


    /**
     * Show all warnings and errors.
     * <p>
     * Any message related to possibly incorrect or unexpected events will be shown.
     */
    WARNING(24),


    /**
     * Show informative messages during processing.
     * <p>
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

    /**
     * Show everything, including trace information.
     */
    TRACE(56);

    private final int code;

    LogLevel(final int code) {
        this.code = code;
    }

    /**
     * @return integer log level code
     */
    public int code() {
        return code;
    }
}
