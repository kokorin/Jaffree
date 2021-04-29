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

/**
 * Nut frame.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class NutFrame {
    /**
     * Stream id.
     */
    public final int streamId;

    /**
     * Presentation timestamp.
     */
    public final long pts;

    /**
     * Frame's data.
     */
    public final byte[] data;

    /**
     * Frame's side data.
     */
    public final DataItem[] sideData;

    /**
     * Frame's metadata.
     */
    public final DataItem[] metaData;

    /**
     * Keyframe mark.
     */
    public final boolean keyframe;

    /**
     * End-of-relevance mark.
     * <p>
     * End of relevance frames indicate that a given stream is not relevant
     * for presentation beginning with the EOR frame and until the following
     * keyframe.
     * <p>
     * This is primarily intended for periods where subtitles are
     * not displayed. But it is not limited to subtitles.
     */
    public final boolean eor;

    /**
     * Creates {@link NutFrame}.
     *
     * @param streamId stream ID
     * @param pts      PTS
     * @param data     data
     * @param sideData side data
     * @param metaData metadata
     * @param keyframe keyframe
     * @param eor      end-of-relevance
     */
    public NutFrame(final int streamId, final long pts,
                    final byte[] data,
                    final DataItem[] sideData, final DataItem[] metaData,
                    final boolean keyframe, final boolean eor) {
        this.streamId = streamId;
        this.pts = pts;
        this.data = data;
        this.sideData = sideData;
        this.metaData = metaData;
        this.keyframe = keyframe;
        this.eor = eor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "NutFrame{"
                + "streamId=" + streamId
                + ", pts=" + pts
                + ", data=" + (data != null ? data.length : "null")
                + ", sideData=" + (sideData != null ? sideData.length : "null")
                + ", metaData=" + (metaData != null ? metaData.length : "null")
                + ", keyframe=" + keyframe
                + ", eor=" + eor
                + '}';
    }
}
