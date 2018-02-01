package com.github.kokorin.jaffree.benchmark;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Warmup(iterations = 0, time = 500, timeUnit = MILLISECONDS)
@Measurement(iterations = 1, time = 200, timeUnit = MILLISECONDS)
@Fork(5)
public class ImageConversion {
    private static int WIDTH = 1024;
    private static int HEIGHT = 768;

    private static byte[] RGB_SRC = new byte[3 * WIDTH * HEIGHT];
    private static byte[] RGBA_SRC = new byte[4 * WIDTH * HEIGHT];
    private static byte[] ABGR_SRC = new byte[4 * WIDTH * HEIGHT];

    static {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int pos = y * WIDTH + x;

                RGB_SRC[pos * 3 + 0] = (byte) (x + y);
                RGB_SRC[pos * 3 + 1] = (byte) ((x + y)/ 10);
                RGB_SRC[pos * 3 + 2] = (byte) (x + y);

                RGBA_SRC[pos * 4 + 0] = (byte) (x + y);
                RGBA_SRC[pos * 4 + 1] = (byte) ((x + y) / 10);
                RGBA_SRC[pos * 4 + 2] = (byte) (x + y);
                RGBA_SRC[pos * 4 + 3] = (byte) 0xFF;

                ABGR_SRC[pos * 4 + 0] = (byte) 0xFF;
                ABGR_SRC[pos * 4 + 1] = (byte) (x + y);
                ABGR_SRC[pos * 4 + 2] = (byte) ((x + y) / 10);
                ABGR_SRC[pos * 4 + 3] = (byte) (x + y);
            }
        }
    }

    @Benchmark
    public BufferedImage RGB_to_3ByteBGR() {
        DataBuffer buffer = new DataBufferByte(RGB_SRC, RGB_SRC.length);

        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, WIDTH, HEIGHT, 3, WIDTH * 3, new int[]{0, 1, 2});
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);

        return image;
    }

    //@Benchmark
    // This algorithm can't be applied to convert images with different number of bands
    /*public BufferedImage RGBA_to_3ByteBGR() {
        DataBuffer buffer = new DataBufferByte(RGBA_SRC, RGBA_SRC.length);

        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, WIDTH, HEIGHT, 4, WIDTH * 4, new int[]{0, 1, 2, 3});
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);

        return image;
    }*/

    //@Benchmark
    // This algorithm can't be applied to convert images with different number of bands
    /*public BufferedImage RGB_to_4ByteABGR() {
        DataBuffer buffer = new DataBufferByte(RGB_SRC, RGB_SRC.length);

        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, WIDTH, HEIGHT, 3, WIDTH * 3, new int[]{0, 1, 2});
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);

        return image;
    }*/

    @Benchmark
    public BufferedImage RGBA_to_4ByteABGR() {
        DataBuffer buffer = new DataBufferByte(RGBA_SRC, RGBA_SRC.length);

        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, WIDTH, HEIGHT, 4, WIDTH * 4, new int[]{0, 1, 2, 3});
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);

        return image;
    }

    @Benchmark
    public BufferedImage ABGR_to_4ByteABGR_arraycopy() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        DataBufferByte buffer = (DataBufferByte)(image.getRaster().getDataBuffer());
        byte[] data = buffer.getData();
        System.arraycopy(ABGR_SRC, 0, data, 0, ABGR_SRC.length);

        return image;
    }

    @Benchmark
    public BufferedImage ABGR_to_4ByteABGR_instantiate() {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel colorModel;
        WritableRaster raster;

        int[] nBits = {8, 8, 8, 8};
        int[] bOffs = {3, 2, 1, 0};
        colorModel = new ComponentColorModel(cs, nBits, true, false,
                Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
        DataBufferByte buffer = new DataBufferByte(ABGR_SRC, ABGR_SRC.length);
        raster = Raster.createInterleavedRaster(buffer,
                WIDTH, HEIGHT,
                WIDTH*4, 4,
                bOffs, null);

        BufferedImage image = new BufferedImage(colorModel, raster, false, null);

        return image;
    }

    @Benchmark
    public BufferedImage BGR_to_3ByteBGR_instantiate() {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] nBits = {8, 8, 8};
        int[] bOffs = {2, 1, 0};
        ColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        DataBufferByte buffer = new DataBufferByte(RGB_SRC, RGB_SRC.length);
        WritableRaster raster = Raster.createInterleavedRaster(buffer,
                WIDTH, HEIGHT,
                WIDTH*3, 3,
                bOffs, null);

        BufferedImage image = new BufferedImage(colorModel, raster, false, null);

        return image;
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
