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

public class SyncPoint {
    /**
     * After a syncpoint, last_pts of each stream is to be set to:
     * last_pts[i] = convert_ts(global_key_pts, time_base[id], time_base[i])
     * <p>
     * global_key_pts MUST be bigger or equal to dts of all past frames across
     * all streams, and smaller or equal to pts of all future frames.
     */
    public final Timestamp globalKeyPts;

    /**
     * back_ptr_div16
     * <p>
     * back_ptr = back_ptr_div16 * 16 + 15
     * <p>
     * back_ptr must point to a position up to 15 bytes before a syncpoint
     * startcode, relative to position of current syncpoint. The syncpoint
     * pointed to MUST be the closest syncpoint such that at least one keyframe
     * with a pts+match_time_delta lower or equal to the current syncpoint's
     * global_key_pts for
     * all streams lies between it and the current syncpoint.
     * <p>
     * A stream where EOR is set is to be ignored for back_ptr.
     */
    public final long backPtrDiv16;

    /**
     * The value of the reference clock at the moment when the first bit of
     * transmit_ts is transmitted/received.
     * The reference clock MUST always be less than or equal to the DTS of
     * every not yet completely received frame.
     */
    public final Timestamp transmitTs;

    public SyncPoint(Timestamp globalKeyPts, long backPtrDiv16) {
        this(globalKeyPts, backPtrDiv16, globalKeyPts);
    }

    public SyncPoint(Timestamp globalKeyPts, long backPtrDiv16, Timestamp transmitTs) {
        this.globalKeyPts = globalKeyPts;
        this.backPtrDiv16 = backPtrDiv16;
        this.transmitTs = transmitTs;
    }
}
