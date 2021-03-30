package com.github.kokorin.jaffree.ffprobe;

/**
 * Marker interface to represent packet, frame or subtitle.
 * <p>
 * Must be implemented only by {@link Packet}, {@link Frame} and {@link Subtitle}.
 */
public interface PacketFrameSubtitle extends FrameSubtitle {
}
