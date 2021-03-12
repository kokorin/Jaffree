package com.github.kokorin.jaffree.util;

import com.github.kokorin.jaffree.LogLevel;

public class LogMessage {
    public final LogLevel logLevel;
    public final String message;

    public LogMessage(LogLevel logLevel, String message) {
        this.logLevel = logLevel;
        this.message = message;
    }
}
