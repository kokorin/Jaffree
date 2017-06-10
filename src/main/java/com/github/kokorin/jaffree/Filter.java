package com.github.kokorin.jaffree;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    private final List<String> inputLinks = new ArrayList<>();
    private String name;
    private final List<String> arguments = new ArrayList<>();
    private final List<String> outputLinks = new ArrayList<>();


    public Filter addInputLink(StreamSpecifier streamSpecifier) {
        this.inputLinks.add(streamSpecifier.getValue());
        return this;
    }

    public Filter addInputLink(String link) {
        this.inputLinks.add(link);
        return this;
    }

    public Filter setName(String name) {
        this.name = name;
        return this;
    }

    public Filter addArgument(String key, String value) {
        this.arguments.add(key + "=" + escape(value));
        return this;
    }

    public Filter addArgumentEscaped(String key, String value) {
        this.arguments.add(key + "=" + value);
        return this;
    }

    public Filter addArgument(String value) {
        this.arguments.add(escape(value));
        return this;
    }

    public Filter addArgumentEscaped(String value) {
        this.arguments.add(value);
        return this;
    }

    public Filter addOutputLink(String link) {
        this.outputLinks.add(link);
        return this;
    }

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

    public static Filter fromInputLink(String link) {
        return new Filter().addInputLink(link);
    }

    public static Filter fromInputLink(StreamSpecifier streamSpecifier) {
        return new Filter().addInputLink(streamSpecifier);
    }

    public static Filter withName(String name) {
        return new Filter().setName(name);
    }

    /**
     * A first level escaping affects the content of each filter option value, which may contain
     * the special character {@code}:{@code} used to separate values, or one of
     * the escaping characters {@code}\'{@code}.
     * @param value
     * @return
     */
    static String escape(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("\\", "\\\\")
                .replace(":", "\\:")
                .replace("'", "\\'");
    }
}
