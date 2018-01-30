package com.github.kokorin.jaffree.benchmark;

import org.junit.Test;

public class ImageConversionTest {

    @Test
    public void RGB_to_3ByteBGR() {
        new ImageConversion().RGB_to_3ByteBGR();
    }

    @Test
    public void RGBA_to_4ByteABGR() {
        new ImageConversion().RGBA_to_4ByteABGR();
    }

    @Test
    public void ABGR_to_4ByteABGR() {
        new ImageConversion().ABGR_to_4ByteABGR_arraycopy();
    }

    @Test
    public void ABGR_to_4ByteABGR_instantiate() {
        new ImageConversion().ABGR_to_4ByteABGR_instantiate();
    }
}