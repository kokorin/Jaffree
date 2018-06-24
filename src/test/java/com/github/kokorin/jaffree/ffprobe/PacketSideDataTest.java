package com.github.kokorin.jaffree.ffprobe;

import org.junit.Test;

/**
 *  This test serves single purpose: to ensure that <b>displaymatrix</b> and <b>rotation</b> attributes
 *  are added to ffprobe.xsd if it is updates
 */
public class PacketSideDataTest {

    @Test
    public void getDisplaymatrix() {
        new PacketSideData().getDisplayMatrix();
    }

    @Test
    public void getRotation() {
        new PacketSideData().getRotation();
    }
}