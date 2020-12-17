package com.github.kokorin.jaffree.ffprobe.data;

import org.jetbrains.annotations.NotNull;

public interface StreamingFormatParser {
    @NotNull
    String getFormatName();
    
    void pushLine(@NotNull String line);
    
    @NotNull
    Data getResult();
}
