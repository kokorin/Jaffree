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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.log.LogMessage;
import com.github.kokorin.jaffree.process.BaseStdReader;
import com.github.kokorin.jaffree.util.ParseUtil;

/**
 * {@link FFmpegResultReader} reads ffmpeg stderr output, parses {@link FFmpegProgress} and
 * {@link FFmpegResult} and passes unparsed output to {@link OutputListener} (if provided).
 */
public class FFmpegResultReader extends BaseStdReader<FFmpegResult> {
    private final OutputListener outputListener;

    /**
     * Creates {@link FFmpegResultReader}.
     *
     * @param outputListener output listener
     */
    public FFmpegResultReader(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FFmpegResult defaultResult() {
        return new FFmpegResult(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FFmpegResult handleLogMessage(final LogMessage logMessage) {
        if (outputListener != null && logMessage.logLevel != null
                && logMessage.logLevel.isInfoOrHigher()) {
            outputListener.onOutput(logMessage.message);
        }

        return ParseUtil.parseResult(logMessage.message);
    }
}
