package com.github.kokorin.jaffree.nut;

public class DataItem {
    public final String name;
    public final Object value;
    public final String type;

    public DataItem(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
