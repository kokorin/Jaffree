package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

public class FilterTest {
    @Test
    public void testGetValue() throws Exception {
        String expected = "[0:1][0:2]amerge";
        String actual = new GenericFilter()
                .addInputLink("0:1")
                .addInputLink("0:2")
                .setName("amerge")
                .getValue();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetValue2() throws Exception {
        // The following graph description will generate a red source with an opacity of 0.2,
        // with size "qcif" and a frame rate of 10 frames per second.
        String expected = "color=c=red@0.2:s=qcif:r=10";
        String actual = new GenericFilter()
                .setName("color")
                .addArgument("c", "red@0.2")
                .addArgument("s", "qcif")
                .addArgument("r", "10")
                .getValue();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEscape() throws Exception {
        Assert.assertEquals("\\\\", GenericFilter.escape("\\"));
        Assert.assertEquals("\\\\\\'", GenericFilter.escape("'"));
        Assert.assertEquals("\\\\:", GenericFilter.escape(":"));

        String text = "this is a 'string': may contain one, or more, special characters";
        String expected = "this is a \\\\\\'string\\\\\\'\\\\: may contain one\\, or more\\, special characters";
        Assert.assertEquals(expected, GenericFilter.escape(text));
    }

}