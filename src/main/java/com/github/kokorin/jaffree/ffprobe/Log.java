/*
 *    Copyright  2018 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffprobe.data.DSection;

/**
 * Logging information from the decoder about each frame.
 *
 * @see FFprobe#setShowLog(LogLevel)
 */
public class Log {
    private final DSection section;

    /**
     * Creates {@link Log}.
     *
     * @param section data section
     */
    public Log(final DSection section) {
        this.section = section;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for
     * the current {@link Log}.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public DSection getSection() {
        return section;
    }

    /**
     * @return context
     */
    public String getContext() {
        return section.getString("context");
    }

    /**
     * @return log level
     */
    // TODO parse enum?
    public Integer getLevel() {
        return section.getInteger("level");
    }

    /**
     * @return category
     */
    public Integer getCategory() {
        return section.getInteger("category");
    }

    /**
     * @return parent context
     */
    public String getParentContext() {
        return section.getString("parent_context");
    }

    /**
     * @return parent category
     */
    public Integer getParentCategory() {
        return section.getInteger("parent_category");
    }

    /**
     * @return message
     */
    public String getMessage() {
        return section.getString("message");
    }
}
