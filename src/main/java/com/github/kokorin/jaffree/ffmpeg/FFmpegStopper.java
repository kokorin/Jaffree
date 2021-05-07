/*
 *    Copyright  2020 Denis Kokorin
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

import com.github.kokorin.jaffree.process.Stopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implement {@link Stopper} to allow forceful and graceful ffmpeg stop.
 */
public class FFmpegStopper implements Stopper {
    private volatile Process process;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegStopper.class);

    /**
     * Stops ffmpeg gracefully by sending &quot;q&quot; character to stdin.
     */
    @Override
    public void graceStop() {
        sendToStdIn("q");
    }

    /**
     * Stops ffmpeg forcefully by sending &quot;qq&quot; character to stdin.
     */
    @Override
    public void forceStop() {
        sendToStdIn("qq");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProcess(final Process process) {
        this.process = process;
    }

    /**
     * Send specified characters to process StdIn.
     *
     * @param chars characters to send
     */
    protected void sendToStdIn(final String chars) {
        if (process == null) {
            LOGGER.error("No Process set yet, can't stop");
            return;
        }

        try (OutputStream stdIn = process.getOutputStream()) {
            LOGGER.debug("Stopping ffmpeg by sending \"{}\" to StdIn", chars);
            stdIn.write(chars.getBytes());
            stdIn.flush();
        } catch (IOException e) {
            LOGGER.info("Ignoring {}: {}", e.getClass(), e.getMessage());
        }
    }
}
