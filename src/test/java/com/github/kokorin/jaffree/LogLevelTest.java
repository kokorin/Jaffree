package com.github.kokorin.jaffree;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogLevelTest {

    @Test
    public void testIsEqualOrHigher() {
        assertTrue(LogLevel.INFO.isEqualOrHigher(LogLevel.DEBUG));
        assertTrue(LogLevel.ERROR.isEqualOrHigher(LogLevel.ERROR));
        assertFalse(LogLevel.DEBUG.isEqualOrHigher(LogLevel.INFO));
    }

    @Test
    public void testIsInfoOrHigher() {
        assertTrue(LogLevel.INFO.isInfoOrHigher());
        assertTrue(LogLevel.ERROR.isInfoOrHigher());
        assertTrue(LogLevel.FATAL.isInfoOrHigher());
        assertTrue(LogLevel.PANIC.isInfoOrHigher());
        assertFalse(LogLevel.DEBUG.isInfoOrHigher());
    }

    @Test
    public void testIsErrorOrHigher() {
        assertTrue(LogLevel.ERROR.isErrorOrHigher());
        assertTrue(LogLevel.FATAL.isErrorOrHigher());
        assertTrue(LogLevel.PANIC.isErrorOrHigher());
        assertFalse(LogLevel.INFO.isErrorOrHigher());
        assertFalse(LogLevel.DEBUG.isErrorOrHigher());
    }
}