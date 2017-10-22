package com.github.kokorin.jaffree.nut;

public class PacketFooter {
    public final long checksum;

    public PacketFooter(long checksum) {
        this.checksum = checksum;
    }
}
