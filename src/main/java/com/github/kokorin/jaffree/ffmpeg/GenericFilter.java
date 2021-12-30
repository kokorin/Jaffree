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

import com.github.kokorin.jaffree.StreamType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents ffmpeg filter.
 * <p>
 * Mainly this class exists to make ffmpeg filter graphs more readable for developers.
 *
 * @see <a href="https://ffmpeg.org/ffmpeg-filters.html">ffmpeg filters documentation</a>
 */
public class GenericFilter implements Filter {
    private final List<String> inputLinks = new ArrayList<>();
    private String name;
    private final List<String> arguments = new ArrayList<>();
    private final List<String> outputLinks = new ArrayList<>();

    /**
     * Adds filter input link.
     *
     * @param streamType stream type
     * @return this
     */
    public GenericFilter addInputLink(final StreamType streamType) {
        this.inputLinks.add(streamType.code());
        return this;
    }

    /**
     * Adds an input link to a filter.
     *
     * @param linkOrStreamSpecifier link name or stream specifier
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    public GenericFilter addInputLink(final String linkOrStreamSpecifier) {
        this.inputLinks.add(linkOrStreamSpecifier);
        return this;
    }

    /**
     * Sets filter to use.
     *
     * @param name filter name
     * @return this
     */
    public GenericFilter setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds filter key-value arguments.
     * <p>
     * Arguments are escaped according to ffmpeg filter graph escaping rules.
     *
     * @param key   key
     * @param value value
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public GenericFilter addArgument(final String key, final String value) {
        this.arguments.add(key + "=" + escape(value));
        return this;
    }

    /**
     * Adds already escaped filter key-value arguments.
     * <p>
     * Passed arguments should be escaped according to ffmpeg filter graph escaping rules.
     *
     * @param key   key
     * @param value value
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public GenericFilter addArgumentEscaped(final String key, final String value) {
        this.arguments.add(key + "=" + value);
        return this;
    }

    /**
     * Adds filter single argument.
     * <p>
     * Argument is escaped according to ffmpeg filter graph escaping rules.
     *
     * @param value value
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public GenericFilter addArgument(final String value) {
        this.arguments.add(escape(value));
        return this;
    }


    /**
     * Adds filter single argument.
     * <p>
     * Passed argument should be escaped according to ffmpeg filter graph escaping rules.
     *
     * @param value value
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public GenericFilter addArgumentEscaped(final String value) {
        this.arguments.add(value);
        return this;
    }

    /**
     * Adds filter output link.
     *
     * @param link outputl link name
     * @return this
     */
    public GenericFilter addOutputLink(final String link) {
        this.outputLinks.add(link);
        return this;
    }

    /**
     * Prints filter description according to ffmpeg filtergraph syntax.
     *
     * @return filter description
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#Filtergraph-syntax-1">
     * filtergraph syntax</a>
     */
    @Override
    public String getValue() {
        StringBuilder result = new StringBuilder();

        for (String inputLink : inputLinks) {
            result.append("[").append(inputLink).append("]");
        }

        result.append(name);

        boolean first = true;
        for (String argument : arguments) {
            if (first) {
                result.append("=");
                first = false;
            } else {
                result.append(":");
            }
            result.append(argument);
        }

        for (String outputLink : outputLinks) {
            result.append("[").append(outputLink).append("]");
        }

        return result.toString();
    }

    /**
     * An escaping affects the content of each filter option value, which may contain the special
     * character.
     * <p>
     * This method implements 1st and 2nd level escaping. 3rd level escaping (shell command) is done
     * by Java Process API.
     *
     * @param value value to be escaped
     * @return escaped value
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    static String escape(final String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("\\", "\\\\")
                .replace(":", "\\\\:")
                .replace(",", "\\,")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace(";", "\\;")
                .replace("'", "\\\\\\'");
    }
}
