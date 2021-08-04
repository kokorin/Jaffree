package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.Artifacts;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class NutReaderTest {

    /**
     * Asserts a bugfix for ArrayIndexOutOfBoundsException in NutReader.
     * This would previously happen when the streamCount was smaller than the timebase count.
     * See https://github.com/kokorin/Jaffree/issues/195 for original bug report.
     */
    @Test
    public void testGetMainHeaderWithExtraTimebase() throws IOException {
        InputStream inputStream = new FileInputStream(Artifacts.VIDEO_NUT_WITH_CHAPTERS.toFile());
        NutReader nutReader = new NutReader(new NutInputStream(inputStream));

        // this would previously throw ArrayIndexOutOfBoundsException
        MainHeader mainHeader = nutReader.getMainHeader();
        assertEquals(2, mainHeader.streamCount);
        assertEquals(3, mainHeader.timeBases.length);
    }
}