/*
 *    Copyright  2017 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.Rational;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class MainHeader {
    public final long majorVersion;
    public final long minorVersion;

    public final int streamCount;

    /**
     * Maximum distance between startcodes. If p1 and p2 are the byte
     * positions of the first byte of two consecutive startcodes, then
     * p2-p1 MUST be less than or equal to max_distance unless the entire
     * span from p1 to p2 comprises a single packet or a syncpoint
     * followed by a single frame. This imposition places efficient upper
     * bounds on seek operations and allows for the detection of damaged
     * frame headers, should a chain of frame headers pass max_distance
     * without encountering any startcode.
     * <p>
     * Syncpoints SHOULD be placed immediately before a keyframe if the
     * previous frame of the same stream was a non-keyframe, unless such
     * non-keyframe - keyframe transitions are very frequent.
     * <p>
     * SHOULD be set to &lt;=32768.
     * If the stored value is &gt;65536 then max_distance MUST be set to 65536.
     * <p>
     * This is also half the maximum frame size without a headerChecksum after the
     * frame header.
     */
    public final long maxDistance;

    public final Rational[] timeBases;
    public final FrameCode[] frameCodes;

    /**
     * For frames with a final size &lt;= 4096 this header is prepended to the
     * frame data. That is if the stored frame is 4000 bytes and the
     * elision_header is 96 bytes then it is prepended, if it is 97 byte then it
     * is not.
     * <p>
     * elision_header[0] is fixed to a length 0 header.
     * <p>
     * The length of each elision_header except header 0 MUST be &lt; 256 and &gt;0.
     * <p>
     * The sum of the lengthes of all elision_headers MUST be &lt;=1024.
     */
    public final long[] elisionHeaderSize;

    public final Set<Flag> flags;

    public MainHeader(long majorVersion, long minorVersion, int streamCount, long maxDistance,
                      Rational[] timeBases, FrameCode[] frameCodes, long[] elisionHeaderSize, Set<Flag> flags) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.streamCount = streamCount;
        this.maxDistance = maxDistance;
        this.timeBases = timeBases;
        this.frameCodes = frameCodes;
        this.elisionHeaderSize = elisionHeaderSize;
        this.flags = Collections.unmodifiableSet(flags);
    }

    @Override
    public String toString() {
        return "MainHeader{" +
                "majorVersion=" + majorVersion +
                ", minorVersion=" + minorVersion +
                ", streamCount=" + streamCount +
                ", maxDistance=" + maxDistance +
                ", timeBases=" + Arrays.toString(timeBases) +
                ", frameCodes=" + Arrays.toString(frameCodes) +
                ", elisionHeaderSize=" + Arrays.toString(elisionHeaderSize) +
                ", flags=" + flags +
                '}';
    }

    public enum Flag {
        /**
         * Set if broadcast mode is in use.
         */
        BROADCAST_MODE(1);

        private final long code;

        Flag(long code) {
            this.code = code;
        }

        public static Set<Flag> fromBitCode(long value) {
            if (value == BROADCAST_MODE.code) {
                return Collections.singleton(BROADCAST_MODE);
            }

            return Collections.emptySet();
        }

        public static long toBitCode(Set<Flag> flags) {
            long result = 0;
            for (Flag flag : flags) {
                result += flag.code;
            }
            return result;
        }
    }
}
