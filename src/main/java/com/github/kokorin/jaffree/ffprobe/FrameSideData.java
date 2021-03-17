/*
 *    Copyright 2018-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * Frame Side Data description.
 *
 * @see FFprobe#setShowFrames(boolean)
 * <p>
 * TODO see ffprobe.c line 2154 - FrameSideData section contains different properties for
 * TODO different side data types.
 */
public class FrameSideData {
    private final ProbeData probeData;

    /**
     * Creates {@link FrameSideData}.
     *
     * @param probeData data section
     */
    public FrameSideData(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe for
     * current {@link FrameSideData}.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters in this class.
     *
     * @return data section
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * Returns side data type.
     * <p>
     * One of the following:
     * <ul>
     * <li>"AVPanScan"</li>
     * <li>"ATSC A53 Part 4 Closed Captions"</li>
     * <li>"Stereoscopic 3d metadata"</li>
     * <li>"AVMatrixEncoding"</li>
     * <li>"Metadata relevant to a downmix procedure"</li>
     * <li>"AVReplayGain"</li>
     * <li>"3x3 displaymatrix"</li>
     * <li>"Active format description"</li>
     * <li>"Motion vectors"</li>
     * <li>"Skip samples"</li>
     * <li>"Audio service type"</li>
     * <li>"Mastering display metadata"</li>
     * <li>"Content light level metadata"</li>
     * <li>"GOP timecode"</li>
     * <li>"ICC profile"</li>
     * </ul>
     *
     * @return side data type
     */
    //TODO introduce enum
    public String getSideDataType() {
        return probeData.getString("side_data_type");
    }

    /**
     * @return side data size
     */
    public Integer getSideDataSize() {
        return probeData.getInteger("side_data_size");
    }

    /**
     * @return timecode
     * <p>
     * TODO check timecode type: integer or rational?
     */
    public String getTimecode() {
        return probeData.getString("timecode");
    }
}
