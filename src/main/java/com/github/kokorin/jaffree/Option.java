package com.github.kokorin.jaffree;

public class Option {
    private final String name;
    private final String value;

    public Option(String name) {
        this(name, null);
    }

    public Option(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
