/*
 *    Copyright 2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg;

import java.awt.image.BufferedImage;

/**
 * Allows conversion of {@link BufferedImage} to byte array and vice versa.
 * <p>
 * Intended for adding custom image formats for programmatic video production and consumption.
 *
 * @see FrameInput
 * @see FrameOutput
 * @see ImageFormats
 */
public interface ImageFormat {
    /**
     * @return pixel format
     */
    String getPixelFormat();

    /**
     * @return bytes per pixel
     */
    int getBytesPerPixel();

    /**
     * Returns FOURCC (four character code).
     *
     * @return FOURCC
     * @see <a href="https://www.fourcc.org/fourcc.php">What is a FOURCC?</a>
     */
    byte[] getFourCC();

    /**
     * Converts byte array to {@link BufferedImage} of specified size.
     *
     * @param data   raw image data
     * @param width  image width
     * @param height image height
     * @return BufferedImage
     */
    BufferedImage toImage(byte[] data, int width, int height);

    /**
     * Converts BufferedImage to raw image data.
     *
     * @param image image
     * @return raw data
     */
    byte[] toBytes(BufferedImage image);
}
