package com.github.kokorin.jaffree.ffprobe.data;

import java.io.InputStream;

public class JsonFormatParser implements FormatParser {
    @Override
    public String getFormatName() {
        return "json";
    }

    @Override
    public Data parse(InputStream inputStream) {
        return null;
    }
}
