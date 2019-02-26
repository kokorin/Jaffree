package com.github.kokorin.jaffree.ffprobe.data;

import java.io.InputStream;

public interface FormatParser {
    String getFormatName();

    Data parse(InputStream inputStream);
}
