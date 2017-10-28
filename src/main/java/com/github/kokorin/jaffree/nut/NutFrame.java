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

public class NutFrame {
    public final int streamId;
    public final long pts;
    public final byte[] data;
    public final DataItem[] sideData;
    public final DataItem[] metaData;
    public final boolean eor;

    public NutFrame(int streamId, long pts, byte[] data, DataItem[] sideData, DataItem[] metaData, boolean eor) {
        this.streamId = streamId;
        this.pts = pts;
        this.data = data;
        this.sideData = sideData;
        this.metaData = metaData;
        this.eor = eor;
    }

    @Override
    public String toString() {
        return "NutFrame{" +
                "streamId=" + streamId +
                ", pts=" + pts +
                ", data=" + (data != null ? data.length : "null") +
                ", sideData=" + (sideData != null ? sideData.length : "null") +
                ", metaData=" + (metaData != null ? metaData.length : "null") +
                ", eor=" + eor +
                '}';
    }
}
