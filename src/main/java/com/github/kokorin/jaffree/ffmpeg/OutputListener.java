package com.github.kokorin.jaffree.ffmpeg;

/**
 * Extend this interface to analyze ffmpeg output
 */
public interface OutputListener {
    /**
     * Invoked on every line, which wasn't parsed by FFmpegResultReader.
     * <p>
     * Attention: this method is not thread safe and may be invoked in different thread.
     * Consider using synchronization.
     *
     * @param line of ffmpeg output, which is neither progress, nor result
     * @return whether input was successfully parsed and should not be treated as error message
     */
    boolean onOutput(String line);
}
