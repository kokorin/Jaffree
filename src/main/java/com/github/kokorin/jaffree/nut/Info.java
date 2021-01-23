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

import java.util.Arrays;

/**
 * Nut format Info packet description.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Info {
    /**
     * Id of stream to which this info should be applied.
     * -1 means Info should be applied to all streams.
     */
    public final int streamId;

    /**
     * The ID of the chapter this packet applies to.
     * <p>
     * If zero, the packet applies to the whole file.
     * Positive chapter_id values represent real chapters and MUST NOT overlap.
     * <p>
     * A negative chapter_id indicates a region of the file and not a real
     * chapter. chapter_id MUST be unique to the region it represents.
     * <p>
     * chapter_id n MUST NOT be used unless there are at least n chapters in the
     * file.
     */
    public final int chapterId;

    /**
     * timestamp of start of chapter.
     * <p>
     * 2 info packets which apply to the same subset of streams SHOULD use the same timebase.
     */
    public final long chapterStartPts;

    /**
     * Length of chapter in the same timebase as chapter_start.
     */
    public final long chapterLengthPts;

    /**
     * Timebase ID for {@link #chapterStartPts} and {@link #chapterLengthPts}.
     */
    public final int timebaseId;

    /**
     * Info packet metadata.
     */
    public final DataItem[] metaData;

    /**
     * Creates Info packet description.
     *
     * @param streamId         stream id
     * @param chapterId        chapter id
     * @param chapterStartPts  chapter start
     * @param chapterLengthPts chapter length
     * @param timebaseId       timebase ID
     * @param metaData         metadata list
     */
    public Info(final int streamId, final int chapterId, final long chapterStartPts,
                final long chapterLengthPts, final int timebaseId, final DataItem[] metaData) {
        this.streamId = streamId;
        this.chapterId = chapterId;
        this.chapterStartPts = chapterStartPts;
        this.chapterLengthPts = chapterLengthPts;
        this.timebaseId = timebaseId;
        this.metaData = metaData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Info{"
                + "streamId=" + streamId
                + ", chapterId=" + chapterId
                + ", chapterStartPts=" + chapterStartPts
                + ", chapterLengthPts=" + chapterLengthPts
                + ", timebaseId=" + timebaseId
                + ", metaData=" + Arrays.toString(metaData)
                + '}';
    }
}
