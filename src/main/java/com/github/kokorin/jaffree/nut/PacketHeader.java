package com.github.kokorin.jaffree.nut;

public class PacketHeader {
    public final long startcode;

    /**
     * Size of the packet data (exactly the distance from the first byte
     * after the packet_header to the first byte of the next packet).
     * <p>
     * Every NUT packet contains a forward_ptr immediately after its startcode
     * with the exception of frame_code-based packets. The forward pointer
     * can be used to skip over the packet without decoding its contents.
     */
    public final long forwardPtr;
    public final long headerChecksum;

    public PacketHeader(long startcode, long forwardPtr, long headerChecksum) {
        this.startcode = startcode;
        this.forwardPtr = forwardPtr;
        this.headerChecksum = headerChecksum;
    }
}
