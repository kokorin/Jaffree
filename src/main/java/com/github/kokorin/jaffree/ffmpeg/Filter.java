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
 * Mainly this class exists to make ffmpeg filters more readable for developers.
 *
 * @see <a href="https://ffmpeg.org/ffmpeg-filters.html">ffmpeg filters documentation</a>
 */
public class Filter {
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
    public Filter addInputLink(final StreamType streamType) {
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
     * @see com.github.kokorin.jaffree.StreamSpecifier
     */
    public Filter addInputLink(final String linkOrStreamSpecifier) {
        this.inputLinks.add(linkOrStreamSpecifier);
        return this;
    }

    /**
     * Sets filter to use.
     *
     * @param name filter name
     * @return this
     */
    public Filter setName(final String name) {
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
    public Filter addArgument(final String key, final String value) {
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
    public Filter addArgumentEscaped(final String key, final String value) {
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
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public Filter addArgument(final String value) {
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
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Notes-on-filtergraph-escaping">
     * filtergraph escaping</a>
     */
    public Filter addArgumentEscaped(final String value) {
        this.arguments.add(value);
        return this;
    }

    /**
     * Adds filter output link.
     *
     * @param link outputl link name
     * @return this
     */
    public Filter addOutputLink(final String link) {
        this.outputLinks.add(link);
        return this;
    }

    /**
     * Prints filter description according to ffmpeg filtergraph syntax.
     *
     * @return filter description
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Filtergraph-syntax-1">
     * filtergraph syntax</a>
     */
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
     * Creates {@link Filter} starting from specified stream type.
     *
     * @param streamType stream type
     * @return Filter
     */
    public static Filter fromInputLink(final StreamType streamType) {
        return new Filter().addInputLink(streamType);
    }

    /**
     * Creates {@link Filter} starting from link name or stream specifier.
     *
     * @param linkOrStreamSpecifier link name or stream specifier
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     * @see com.github.kokorin.jaffree.StreamSpecifier
     */
    public static Filter fromInputLink(final String linkOrStreamSpecifier) {
        return new Filter().addInputLink(linkOrStreamSpecifier);
    }

    /**
     * Creates {@link Filter} starting from filter name.
     *
     * @param name filter
     * @return Filter
     */
    public static Filter withName(final String name) {
        return new Filter().setName(name);
    }

    /**
     * A first level escaping affects the content of each filter option value, which may contain
     * the special character {@code}:{@code} used to separate values, or one of
     * the escaping characters {@code}\'{@code}.
     *
     * @param value value to be escaped
     * @return escaped value
     */
    static String escape(final String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("\\", "\\\\")
                .replace(":", "\\:")
                .replace("'", "\\'");
    }
}
