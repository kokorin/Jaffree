package com.github.kokorin.jaffree.nut;

import java.util.EnumSet;
import java.util.Set;

public class FrameTable {
    public final Set<Flag> flags;

    public final long streamId;

    /**
     * If FLAG_SIZE_MSB is set then data_size_msb which is stored after the
     * frame code is multiplied with it and forms the more significant part
     * of the size of the following frame.
     * <p>
     * If FLAG_SIZE_MSB is not set then this field has no meaning.
     * MUST be <16384.
     */
    public final long dataSizeMul;

    /**
     * The less significant part of the size of the following frame.
     * This added together with data_size_mul*data_size_msb is the size of
     * the following frame.
     * MUST be <16384.
     */
    public final long dataSizeLsb;

    /**
     * If FLAG_CODED_PTS is set in the flags of the current frame then this
     * value MUST be ignored, if FLAG_CODED_PTS is not set then pts_delta is the
     * difference between the current pts and last_pts.
     * MUST be <16384 and >-16384.
     */
    public final long ptsDelta;

    /**
     * MUST be <256.
     */
    public final long reservedCount;

    /**
     * This is the time difference in stream timebase units from the pts at which
     * the output from the decoder has converged independent from the availability
     * of previous frames (that is the frames are virtually identical no matter
     * if decoding started from the very first frame or from this keyframe).
     * <p>
     * If its value is 1-(1<<62) then match_time_delta is unspecified, that is
     * the muxer lacked sufficient information to set it.
     * <p>
     * A muxer MUST only set it to 1-(1<<62) if it does not know the correct
     * value. That is, it is not allowed to randomly discard known values.
     * <p>
     * If FLAG_MATCH_TIME is not set then this value shall be used for
     * match_time_delta, otherwise this value is ignored.
     * MUST be <32768 and >-32768 or =1-(1<<62).
     */
    public final long matchTimeDelta;

    /**
     * The index into the elision_header table. MUST be <128.
     */
    public final long headerIdx;


    public static final FrameTable INVALID = new FrameTable(EnumSet.of(Flag.INVALID), 0, 0, 0, 0, 0, 0, 0);


    public FrameTable(Set<Flag> flags, long streamId, long dataSizeMul, long dataSizeLsb, long ptsDelta, long reservedCount, long matchTimeDelta, long headerIdx) {
        this.flags = flags;
        this.streamId = streamId;
        this.dataSizeMul = dataSizeMul;
        this.dataSizeLsb = dataSizeLsb;
        this.ptsDelta = ptsDelta;
        this.reservedCount = reservedCount;
        this.matchTimeDelta = matchTimeDelta;
        this.headerIdx = headerIdx;
    }

    public enum Flag {
        KEYFRAME(1),

        /**
         * If set, the stream has no relevance on presentation. (EOR)
         * <p>
         * EOR frames MUST be zero-length and must be set keyframe.
         * All streams SHOULD end with EOR, where the pts of the EOR indicates the
         * end presentation time of the final frame.
         * <p>
         * An EOR set stream is unset by the first content frame.
         * EOR can only be unset in streams with zero decode_delay .
         */
        EOR(1 << 1),

        /**
         * If set, coded_pts is in the frame header.
         */
        CODED_PTS(1 << 3),

        /**
         * If set, stream_id is coded in the frame header.
         */
        STREAM_ID(1 << 4),

        /**
         * If set, data_size_msb is coded in the frame header, otherwise data_size_msb is 0.
         */
        SIZE_MSB(1 << 5),

        /**
         * If set, the frame header contains a headerChecksum.
         * <p>
         * MUST be set if the frame's data_size is strictly greater than
         * 2*max_distance or the difference abs(pts-last_pts) is strictly greater than
         * max_pts_distance (where pts represents this frame's pts and last_pts is
         * defined as below).
         */
        CHECKSUM(1 << 6),

        /**
         * If set, reserved_count is coded in the frame header.
         */
        RESERVED(1 << 7),

        /**
         * If set, side/meta data is stored with the frame data. This flag MUST NOT be set in version < 4
         */
        SM_DATA(1 << 8),

        /**
         * If set, header_idx is coded in the frame header.
         */
        HEADER_IDX(1 << 10),

        /**
         * If set, match_time_delta is coded in the frame header
         */
        MATCH_TIME(1 << 11),

        /**
         * If set, coded_flags are stored in the frame header.
         */
        CODED(1 << 12),

        /**
         * If set, frame_code is invalid.
         */
        INVALID(1 << 13);

        private final long code;

        Flag(long code) {
            this.code = code;
        }

        public static Set<Flag> fromBitCode(long value) {
            Set<Flag> result = EnumSet.noneOf(Flag.class);
            for (Flag flag : values()) {
                if ((flag.code & value) > 0) {
                    result.add(flag);
                }
            }
            return result;
        }
    }
}
