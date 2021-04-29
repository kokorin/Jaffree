package com.github.kokorin.jaffree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OSTest {
    @Test
    public void osDetected() {
        int count = 0;

        if (OS.IS_LINUX) {
            count++;
        }
        if (OS.IS_MAC) {
            count++;
        }
        if (OS.IS_WINDOWS) {
            count++;
        }

        assertEquals("Exactly one property is true: " + OS.OS_NAME, 1, count);
    }
}