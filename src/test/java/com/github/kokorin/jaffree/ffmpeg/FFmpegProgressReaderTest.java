package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FFmpegProgressReaderTest {

    @Test
    public void readProgress() throws Exception {
        final List<FFmpegProgress> progressList = new ArrayList<>();

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                progressList.add(progress);
            }
        };

        FFmpegProgressReader reader = new FFmpegProgressReader(listener);
        try (InputStream inputStream = getClass().getResourceAsStream("progress.log")) {
            reader.readProgress(inputStream);
        }

        Assert.assertEquals(3, progressList.size());

        FFmpegProgress progress = progressList.get(0);

        Assert.assertEquals((Long) 162L, progress.getFrame());
        Assert.assertEquals((Double) 0., progress.getFps());
        Assert.assertEquals((Double) 29., progress.getQ());
        Assert.assertEquals((Double) 828.7, progress.getBitrate());
        Assert.assertEquals((Long) 524_336L, progress.getSize());
        Assert.assertEquals((Long) 5L, progress.getTime(TimeUnit.SECONDS));
        Assert.assertEquals((Long) 5_061L, progress.getTimeMillis());
        Assert.assertEquals((Long) 5_061L, progress.getTime(TimeUnit.MILLISECONDS));
        Assert.assertEquals((Long) 5_061_950L, progress.getTimeMicros());
        Assert.assertEquals((Long) 5_061_950L, progress.getTime(TimeUnit.MICROSECONDS));
        Assert.assertEquals((Long) 4L, progress.getDup());
        Assert.assertEquals((Long) 2L, progress.getDrop());
        Assert.assertEquals((Double) 10.1, progress.getSpeed());

        progress = progressList.get(1);

        Assert.assertEquals((Long) 2240L, progress.getFrame());
        Assert.assertEquals((Double) 279.07, progress.getFps());
        Assert.assertEquals((Double) 28., progress.getQ());
        Assert.assertEquals((Double) 1125.4, progress.getBitrate());
        Assert.assertEquals((Long) 10_485_808L, progress.getSize());
        Assert.assertEquals((Long) 74_536_054L, progress.getTimeMicros());
        Assert.assertEquals((Long) 0L, progress.getDup());
        Assert.assertEquals((Long) 0L, progress.getDrop());
        Assert.assertEquals((Double) 9.29, progress.getSpeed());
    }

    /**
     * Tests progress that has N/A values (first pass in 2 pass encoding for example)
     */
    @Test
    public void readProgressNA() throws Exception {
        final List<FFmpegProgress> progressList = new ArrayList<>();

        ProgressListener listener = progressList::add;

        FFmpegProgressReader reader = new FFmpegProgressReader(listener);
        try (InputStream inputStream = getClass().getResourceAsStream("progress-na.log")) {
            reader.readProgress(inputStream);
        }

        Assert.assertEquals(3, progressList.size());

        FFmpegProgress progress = progressList.get(0);

        Assert.assertEquals((Long) 1L, progress.getFrame());
        Assert.assertEquals((Double) 0., progress.getFps());
        Assert.assertEquals((Double) 0., progress.getQ());
        Assert.assertNull(progress.getBitrate());
        Assert.assertNull(progress.getSize());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.SECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMillis());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MILLISECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMicros());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MICROSECONDS));
        Assert.assertEquals((Long) 0L, progress.getDup());
        Assert.assertEquals((Long) 0L, progress.getDrop());
        Assert.assertEquals((Double) 0., progress.getSpeed());

        progress = progressList.get(1);

        Assert.assertEquals((Long) 7L, progress.getFrame());
        Assert.assertEquals((Double) 0., progress.getFps());
        Assert.assertEquals((Double) 0., progress.getQ());
        Assert.assertNull(progress.getBitrate());
        Assert.assertNull(progress.getSize());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.SECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMillis());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MILLISECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMicros());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MICROSECONDS));
        Assert.assertEquals((Long) 0L, progress.getDup());
        Assert.assertEquals((Long) 0L, progress.getDrop());
        Assert.assertEquals((Double) 0., progress.getSpeed());

        progress = progressList.get(2);

        Assert.assertEquals((Long) 17L, progress.getFrame());
        Assert.assertEquals((Double) 14.77, progress.getFps());
        Assert.assertEquals((Double) 0., progress.getQ());
        Assert.assertNull(progress.getBitrate());
        Assert.assertNull(progress.getSize());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.SECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMillis());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MILLISECONDS));
        Assert.assertEquals((Long) 0L, progress.getTimeMicros());
        Assert.assertEquals((Long) 0L, progress.getTime(TimeUnit.MICROSECONDS));
        Assert.assertEquals((Long) 0L, progress.getDup());
        Assert.assertEquals((Long) 0L, progress.getDrop());
        Assert.assertEquals((Double) 0., progress.getSpeed());
    }
}