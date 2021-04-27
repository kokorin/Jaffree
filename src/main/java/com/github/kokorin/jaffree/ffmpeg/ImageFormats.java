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

import com.github.kokorin.jaffree.JaffreeException;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public enum ImageFormats implements ImageFormat {
    BGR24(
            "bgr24",
            3,
            new byte[]{'B', 'G', 'R', 24},
            BufferedImage.TYPE_3BYTE_BGR,
            new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    new int[]{8, 8, 8}, false, false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE
            ),
            new int[]{2, 1, 0}
    ),
    ABGR(
            "abgr",
            4,
            new byte[]{'A', 'B', 'G', 'R'},
            BufferedImage.TYPE_4BYTE_ABGR,
            new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    new int[]{8, 8, 8, 8}, true, false,
                    Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE
            ),
            new int[]{3, 2, 1, 0}
    );

    private final String pixelFormat;
    private final int bytesPerPixel;
    private final byte[] fourcc;
    private final int imageType;
    private final ComponentColorModel componentColorModel;
    private final int[] bOffs;

    ImageFormats(String pixelFormat, int bytesPerPixel, byte[] fourcc, int imageType,
                 ComponentColorModel componentColorModel, int[] bOffs) {
        this.pixelFormat = pixelFormat;
        this.bytesPerPixel = bytesPerPixel;
        this.fourcc = fourcc;
        this.imageType = imageType;
        this.componentColorModel = componentColorModel;
        this.bOffs = bOffs;
    }

    @Override
    public String getPixelFormat() {
        return pixelFormat;
    }

    @Override
    public int getBytesPerPixel() {
        return bytesPerPixel;
    }

    @Override
    public byte[] getFourCC() {
        return fourcc.clone();
    }

    @Override
    public BufferedImage toImage(byte[] data, int width, int height) {
        int expectedLength = width * height * bytesPerPixel;
        if (data.length != expectedLength) {
            throw new JaffreeException("Not enough bytes: " + data.length + ", expected " + expectedLength);
        }

        DataBuffer buffer = new DataBufferByte(data, data.length);
        WritableRaster raster = Raster.createInterleavedRaster(
                buffer, width, height,
                width * bytesPerPixel, bytesPerPixel,
                bOffs, null
        );

        return new BufferedImage(componentColorModel, raster, false, null);
    }

    @Override
    public byte[] toBytes(BufferedImage image) {
        if (image.getType() != imageType) {
            throw new JaffreeException("Wrong image type: " + image.getType() + ", expected: " + imageType);
        }

        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }
}
