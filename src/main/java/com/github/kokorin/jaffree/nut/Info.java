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

public class Info {
    /**
     * Id of stream to which this info should be applied.
     * -1 means Info should be applied to all streams.
     */
    public final int streamId;
    public final int chapterId;
    public final long chapterStartPts;
    public final long chapterLengthPts;
    public final int timebaseId;
    public final DataItem[] metaData;

    public Info(int streamId, int chapterId, long chapterStart, long chapterLength, int timebaseId, DataItem[] metaData) {
        this.streamId = streamId;
        this.chapterId = chapterId;
        this.chapterStartPts = chapterStart;
        this.chapterLengthPts = chapterLength;
        this.timebaseId = timebaseId;
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "Info{" +
                "streamId=" + streamId +
                ", chapterId=" + chapterId +
                ", chapterStartPts=" + chapterStartPts +
                ", chapterLengthPts=" + chapterLengthPts +
                ", timebaseId=" + timebaseId +
                ", metaData=" + Arrays.toString(metaData) +
                '}';
    }
}
