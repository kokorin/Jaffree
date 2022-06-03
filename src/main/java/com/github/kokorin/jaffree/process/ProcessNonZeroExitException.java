/*
 *    Copyright 2022 Jon Frydensbjerg
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

package com.github.kokorin.jaffree.process;

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.log.LogMessage;

import java.util.List;

/**
 * Non-zero status code exit exception which includes all error messages produced by the process.
 */
public class ProcessNonZeroExitException extends JaffreeException {
    private List<LogMessage> processErrorLogMessages;

    /**
     * Constructs a new {@link ProcessNonZeroExitException} with the specified detail message
     * and additional context.
     *
     * @param message message
     * @param processErrorLogMessages error log messages produced by the process
     */
    public ProcessNonZeroExitException(final String message,
                                       final List<LogMessage> processErrorLogMessages) {
        super(message);

        this.processErrorLogMessages = processErrorLogMessages;
    }

    /**
     * Return the list of error log messages.
     *
     * @return error log messages
     */
    public List<LogMessage> getProcessErrorLogMessages() {
        return processErrorLogMessages;
    }
}
