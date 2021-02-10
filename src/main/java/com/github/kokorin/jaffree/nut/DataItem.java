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
 * This data structure is used both in frames for per frame side and metadata
 * as well as info tags for metadata covering the whole file, a stream
 * chapter or other.
 * <p>
 * Metadata is data that is about the actual data and generally not essential
 * for correct presentation
 * <p>
 * Sidedata is semantically part of the data and essential for its correct
 * presentation. The same syntax is used by both for simplicity.
 * <p>
 * Types of per frame side data:
 * <uL>
 * <li>"Channels", "ChannelLayout", "SampleRate", "Width", "Height" --
 * This frame changes the number of channels, the channel layout, ... to
 * the given value (ChannelLayout vb, else v)
 * If used in any frame of a stream then every keyframe of the stream
 * SHOULD carry such sidedata to allow seeking.</li>
 * <li>"Extradata", "Palette" --
 * This frame changes the codec_specific_data or palette to the given
 * value (vb)
 * If used in any frame of a stream then every keyframe of the stream
 * SHOULD carry such sidedata to allow seeking.</li>
 * <li>"CodecSpecificSide&lt;num&gt;" --
 * Codec specific side data, equivalent to matroskas BlockAdditional (vb)
 * the "&lt;num&gt;" should be replaced by a number identifying the type of
 * side data, it is equivalent/equal to BlockAddId in matroska.</li>
 * <li>"SkipStart", "SkipEnd" --
 * The decoder should skip/drop the specified number of samples at the
 * start/end of this frame (v)</li>
 *
 * <li>"UserData&lt;identifer here&gt;" --
 * User specific side data, the "&lt;identifer here\&gt;" should be replaced
 * by a globally unique identifer of the project that
 * uses/creates/understands the side data. For example "UserDataFFmpeg"</li>
 * </uL>
 * <p>
 * Nut specification references to this data as <b>side/meta data</b> or <b>sm_data</b>
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class DataItem {
    public final String name;
    public final Object value;
    // TODO: introduce type enum?
    public final String type;

    /**
     * Creates {@link DataItem}.
     *
     * @param name  name
     * @param value value
     * @param type  type
     */
    public DataItem(final String name, final Object value, final String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DataItem{"
                + "name='" + name + '\''
                + ", value=" + value
                + ", type='" + type + '\''
                + '}';
    }
}
