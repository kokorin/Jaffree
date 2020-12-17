package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.StreamingFormatParser;
import com.github.kokorin.jaffree.process.LinesProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class FFprobeProcessHandler extends LinesProcessHandler<FFprobeResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobeProcessHandler.class);
    
    private final StreamingFormatParser parser;
    
    public FFprobeProcessHandler(@NotNull StreamingFormatParser parser) {
        Objects.requireNonNull(parser, "parser must not be null");
        
        this.parser = parser;
    }
    
    @Override
    public void onStderrLine(@NotNull String line) {
        LOGGER.error(line);
        setException(new RuntimeException(line));
    }
    
    @Override
    public void onStdoutLine(@NotNull String line) {
        try {
            parser.pushLine(line);
        } catch (Exception x) {
            setException(x);
        }
    }
    
    @Override
    public void onExit() {
        try {
            setResult(new FFprobeResult(parser.getResult()));
        } catch (Exception x) {
            setException(x);
        }
    }
}
