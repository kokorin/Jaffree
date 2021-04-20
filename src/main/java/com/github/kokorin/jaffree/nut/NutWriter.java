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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.nut.FrameCode.Flag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NutWriter {
    private final NutOutputStream output;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private MainHeader mainHeader;
    private StreamHeader[] streamHeaders;
    private Info[] infos;
    private long[] lastPts;
    // true if last frame of the corresponding stream was EOR frame
    private boolean[] eor;
    private long lastSyncPointPosition = 0;

    private boolean initialized = false;
    private boolean closed = false;

    private long frameOrderingBufferMillis = 200;

    private final List<TsFrame> frameOrderingBuffer = new ArrayList<>();

    private static final long MAJOR_VERSION = 3;
    private static final long MINOR_VERSION = 0;

    public NutWriter(NutOutputStream output) {
        this.output = output;
    }

    public void setMainHeader(int streamCount, long maxDistance, Rational[] timebases, FrameCode[] frameCodes) {
        if (initialized) {
            throw new JaffreeException("NutWriter is already initialized!");
        }
        this.mainHeader = new MainHeader(
                MAJOR_VERSION,
                MINOR_VERSION,
                streamCount,
                maxDistance,
                timebases,
                frameCodes,
                new long[0],
                EnumSet.noneOf(MainHeader.Flag.class)
        );
    }

    public void setStreamHeaders(StreamHeader[] streamHeaders) {
        if (initialized) {
            throw new JaffreeException("NutWriter is already initialized!");
        }
        this.streamHeaders = streamHeaders;
    }

    public void setInfos(Info[] infos) {
        if (initialized) {
            throw new JaffreeException("NutWriter is already initialized!");
        }
        this.infos = infos;
    }

    /**
     * By default 200 milliseconds.
     *
     * @param frameOrderingBufferMillis size of frame ordering buffer in milliseconds
     */
    public void setFrameOrderingBufferMillis(long frameOrderingBufferMillis) {
        this.frameOrderingBufferMillis = frameOrderingBufferMillis;
    }

    private void initialize() throws IOException {
        if (initialized) {
            return;
        }

        lastPts = new long[mainHeader.streamCount];
        eor = new boolean[mainHeader.streamCount];

        output.writeCString(NutConst.FILE_ID);
        writeMainHeader();
        if (streamHeaders == null) {
            throw new JaffreeException("StreamHeaders must be specified before");
        }
        for (StreamHeader streamHeader : streamHeaders) {
            writeStreamHeader(streamHeader);
        }

        if (infos == null) {
            throw new IllegalArgumentException("Info array must be specified");
        }
        for (Info info : infos) {
            writeInfo(info);
        }

        writeSyncPoint();

        initialized = true;
    }

    private void writeMainHeader() throws IOException {
        if (mainHeader == null) {
            throw new JaffreeException("MainHeader must be specified before");
        }

        buffer.reset();
        // Temp buffer, used to calculate data size
        NutOutputStream bufOutput = new NutOutputStream(buffer);

        bufOutput.writeValue(MAJOR_VERSION);
        if (MAJOR_VERSION > 3) {
            bufOutput.writeValue(MINOR_VERSION);
        }

        bufOutput.writeValue(mainHeader.streamCount);
        bufOutput.writeValue(mainHeader.maxDistance);
        bufOutput.writeValue(mainHeader.timeBases.length);

        Rational[] timeBases = mainHeader.timeBases;
        for (Rational timeBase : timeBases) {
            bufOutput.writeValue(timeBase.numerator);
            bufOutput.writeValue(timeBase.denominator);
        }

        int fields, streamId = 0, size;
        long ptsDelta = 0, dataSizeMul = 1;
        Set<Flag> flags;

        for (int i = 0; i < 256; ) {
            fields = 0;
            FrameCode frameCode = mainHeader.frameCodes[i];
            flags = frameCode.flags;

            if (frameCode.ptsDelta != ptsDelta) {
                fields = 1;
            }
            ptsDelta = frameCode.ptsDelta;

            if (frameCode.dataSizeMul != dataSizeMul) {
                fields = 2;
            }
            dataSizeMul = frameCode.dataSizeMul;

            if (frameCode.streamId != streamId) {
                fields = 3;
            }
            streamId = frameCode.streamId;

            if (frameCode.dataSizeLsb != 0) {
                fields = 4;
            }
            size = frameCode.dataSizeLsb;

            int count;
            for (count = 0; i < 256; count++, i++) {
                if (i == 'N') {
                    count--;
                    continue;
                }

                frameCode = mainHeader.frameCodes[i];
                boolean flagsAreEqual = frameCode.flags.containsAll(flags) && frameCode.flags.size() == flags.size();
                if (!flagsAreEqual) {
                    break;
                }
                if (frameCode.streamId != streamId) {
                    break;
                }
                if (frameCode.dataSizeMul != dataSizeMul) {
                    break;
                }
                if (frameCode.dataSizeLsb != size + count) {
                    break;
                }
                if (frameCode.ptsDelta != ptsDelta) {
                    break;
                }
            }

            if (count != dataSizeMul - size) {
                fields = 6;
            }

            bufOutput.writeValue(Flag.toBitCode(flags));
            bufOutput.writeValue(fields);
            if (fields > 0) {
                bufOutput.writeSignedValue(ptsDelta);
            }
            if (fields > 1) {
                bufOutput.writeValue(dataSizeMul);
            }
            if (fields > 2) {
                bufOutput.writeValue(streamId);
            }
            if (fields > 3) {
                bufOutput.writeValue(size);
            }
            if (fields > 4) {
                bufOutput.writeValue(0); // reserved length
            }
            if (fields > 5) {
                bufOutput.writeValue(count);
            }
        }

        bufOutput.writeValue(0); // elision header_count_minus1
        bufOutput.writeValue(0); // main_flags

        bufOutput.flush();
        writePacket(NutConst.MAIN_STARTCODE, buffer.toByteArray());
    }

    private void writeStreamHeader(StreamHeader streamHeader) throws IOException {
        buffer.reset();
        // Temp buffer, used to calculate data size
        NutOutputStream bufOutput = new NutOutputStream(buffer);

        bufOutput.writeValue(streamHeader.streamId);
        bufOutput.writeValue(streamHeader.streamType.code);
        bufOutput.writeVariableBytes(streamHeader.fourcc);
        bufOutput.writeValue(streamHeader.timeBaseId);
        bufOutput.writeValue(streamHeader.msbPtsShift);
        bufOutput.writeValue(streamHeader.maxPtsDistance);
        bufOutput.writeValue(streamHeader.decodeDelay);
        bufOutput.writeValue(StreamHeader.Flag.toBitCode(streamHeader.flags));
        bufOutput.writeVariableBytes(streamHeader.codecSpecificData);

        if (streamHeader.streamType == StreamHeader.Type.VIDEO) {
            bufOutput.writeValue(streamHeader.video.width);
            bufOutput.writeValue(streamHeader.video.height);
            bufOutput.writeValue(streamHeader.video.sampleWidth);
            bufOutput.writeValue(streamHeader.video.sampleHeight);
            bufOutput.writeValue(streamHeader.video.type.code);
        } else if (streamHeader.streamType == StreamHeader.Type.AUDIO) {
            bufOutput.writeValue(streamHeader.audio.samplerate.numerator);
            bufOutput.writeValue(streamHeader.audio.samplerate.denominator);
            bufOutput.writeValue(streamHeader.audio.channelCount);
        }

        bufOutput.flush();
        writePacket(NutConst.STREAM_STARTCODE, buffer.toByteArray());
    }

    /**
     * Writes frame to underlying OutputStream.
     * <p>
     * Note: When all frames are passed to this method the caller MUST invoke {@link #writeFooter()}
     * <p>
     * Note: frames are not written immediately to stream, instead they are buffered, reordered and than written.
     *
     * @param frame frame to write
     * @throws IOException
     * @see #setFrameOrderingBufferMillis(long)
     */
    public void writeFrame(NutFrame frame) throws IOException {
        if (closed) {
            throw new JaffreeException("NutWriter is closed");
        }

        StreamHeader stream = streamHeaders[frame.streamId];
        Rational timestamp = mainHeader.timeBases[stream.timeBaseId].multiply(frame.pts);
        frameOrderingBuffer.add(new TsFrame(timestamp, frame));
        Collections.sort(frameOrderingBuffer, TsFrame.COMPARATOR);

        Rational lastFrameTimestamp = frameOrderingBuffer.get(frameOrderingBuffer.size() - 1).timestamp;
        // Check if we have to remove some frames from buffer and to write them to ouput
        Iterator<TsFrame> frameIterator = frameOrderingBuffer.iterator();
        while (frameIterator.hasNext()) {
            TsFrame tsFrame = frameIterator.next();
            // current frame can't be written yet, as well as all subsequent
            if (lastFrameTimestamp.subtract(tsFrame.timestamp).lessThanOrEqual(new Rational(frameOrderingBufferMillis, 1000))) {
                break;
            }

            writeFrameInternal(tsFrame.frame);
            frameIterator.remove();
        }
    }

    private void writeFrameInternal(NutFrame frame) throws IOException {
        initialize();

        // EOR frames by specification use TS of the previous frame in the same stream.
        if (!frame.eor) {
            Rational maxTs = Rational.ZERO;
            for (int i = 0; i < mainHeader.timeBases.length; i++) {
                Rational ts = mainHeader.timeBases[i].multiply(lastPts[i]);
                if (ts.greaterThan(maxTs)) {
                    maxTs = ts;
                }
            }
            StreamHeader steam = streamHeaders[frame.streamId];
            Rational framedTs = mainHeader.timeBases[steam.timeBaseId].multiply(frame.pts);
            if (framedTs.lessThan(maxTs)) {
                throw new JaffreeException("Unordered frames! Try to increase frameOrderingBufferMillis. maxTs: " + maxTs + ", but current: " + framedTs);
            }
        }

        StreamHeader sc = streamHeaders[frame.streamId];

        int i, ftnum = -1, size = 0, msb_pts = (1 << sc.msbPtsShift);
        Set<Flag> codedFlags = Collections.emptySet();
        long coded_pts, pts_delta = frame.pts - lastPts[frame.streamId];
        boolean checksum = false;

        if (Math.abs(pts_delta) < (msb_pts / 2) - 1) {
            coded_pts = frame.pts & (msb_pts - 1);
        } else {
            coded_pts = frame.pts + msb_pts;
        }

        if (frame.data.length > 2 * mainHeader.maxDistance) {
            checksum = true;
        }
        if (Math.abs(pts_delta) > sc.maxPtsDistance) {
            checksum = true;
        }

        for (i = 0; i < 256; i++) {
            int len = 1; // frame code
            FrameCode ft = mainHeader.frameCodes[i];

            Set<Flag> flags = ft.flags;
            if (flags.contains(Flag.INVALID)) {
                continue;
            }

            Set<Flag> fdFlags = EnumSet.noneOf(Flag.class);
            if (frame.keyframe) {
                fdFlags.add(Flag.KEYFRAME);
            }
            if (frame.eor) {
                fdFlags.add(Flag.EOR);
            }

            if (flags.contains(Flag.CODED_FLAGS)) {
                flags = EnumSet.copyOf(fdFlags);

                if (ft.streamId != frame.streamId) {
                    flags.add(Flag.STREAM_ID);
                }
                if (ft.ptsDelta != pts_delta) {
                    flags.add(Flag.CODED_PTS);
                }
                if (ft.dataSizeLsb != frame.data.length) {
                    flags.add(Flag.SIZE_MSB);
                }
                if (checksum) {
                    flags.add(Flag.CHECKSUM);
                }
                flags.add(Flag.CODED_FLAGS);
            }


            // if ((flags ^ fd -> flags) & NUT_API_FLAGS) continue;
            Set<Flag> xor = Flag.xor(flags, fdFlags);
            if (xor.contains(Flag.KEYFRAME) || xor.contains(Flag.EOR)) {
                continue;
            }

            if (!flags.contains(Flag.STREAM_ID) && ft.streamId != frame.streamId) {
                continue;
            }

            if (!flags.contains(Flag.CODED_PTS) && ft.ptsDelta != pts_delta) {
                continue;
            }

            if (flags.contains(Flag.SIZE_MSB)) {
                if ((frame.data.length - ft.dataSizeLsb) % ft.dataSizeMul != 0) {
                    continue;
                }
            } else {
                if (ft.dataSizeLsb != frame.data.length) {
                    continue;
                }
            }

            if (!flags.contains(Flag.CHECKSUM) && checksum) {
                continue;
            }

            // it doesn't fully follow specification, but is simple enough
            if (flags.contains(Flag.CODED_FLAGS)) {
                len += 8;
            }
            if (flags.contains(Flag.STREAM_ID)) {
                len += 8;
            }
            if (flags.contains(Flag.CODED_PTS)) {
                len += 8;
            }
            if (flags.contains(Flag.SIZE_MSB)) {
                len += 8;
            }
            if (flags.contains(Flag.CHECKSUM)) {
                len += 4;
            }

            if (size == 0 || len < size) {
                ftnum = i;
                codedFlags = flags;
                size = len;
            }
        }

        if (ftnum == -1) {
            throw new IllegalArgumentException("Can't find appropriate FrameCode for " + frame);
        }

        // Distance between synpoints (in bytes) should be no more that maxDistance
        if (lastSyncPointPosition + mainHeader.maxDistance < output.getPosition() + size + frame.data.length) {
            writeSyncPoint();
        }

        output.resetCrc32();
        output.writeByte(ftnum);
        FrameCode ft = mainHeader.frameCodes[ftnum];
        if (codedFlags.contains(Flag.CODED_FLAGS)) {
            Set<Flag> codedXor = Flag.xor(codedFlags, ft.flags);
            output.writeValue(Flag.toBitCode(codedXor));
        }
        if (codedFlags.contains(Flag.STREAM_ID)) {
            output.writeValue(frame.streamId);
        }
        if (codedFlags.contains(Flag.CODED_PTS)) {
            output.writeValue(coded_pts);
        }
        if (codedFlags.contains(Flag.SIZE_MSB)) {
            output.writeValue((frame.data.length - ft.dataSizeLsb) / ft.dataSizeMul);
        }
        if (codedFlags.contains(Flag.CHECKSUM)) {
            output.writeCrc32();
        }

        // elision headers?
        output.writeBytes(frame.data);

        lastPts[frame.streamId] = frame.pts;
        eor[frame.streamId] = codedFlags.contains(Flag.EOR);
    }

    public void writeFooter() throws IOException {
        // writeEorFrame uses lastPts, it is updated by writeFrameInternal
        for (TsFrame tsFrame : frameOrderingBuffer) {
            writeFrameInternal(tsFrame.frame);
        }
        frameOrderingBuffer.clear();

        for (int streamId = 0; streamId < eor.length; streamId++) {
            if (!eor[streamId]) {
                writeEorFrame(streamId);
            }
        }

        for (TsFrame tsFrame : frameOrderingBuffer) {
            writeFrameInternal(tsFrame.frame);
        }
        frameOrderingBuffer.clear();

        writeMainHeader();
        for (StreamHeader streamHeader : streamHeaders) {
            writeStreamHeader(streamHeader);
        }

        writeSyncPoint();

        closed = true;
    }

    private void writeEorFrame(int streamId) throws IOException {
        NutFrame frame = new NutFrame(streamId, lastPts[streamId], new byte[0], null, null, true, true);
        writeFrame(frame);
    }


    private void writeInfo(Info info) throws IOException {
        buffer.reset();
        // Temp buffer, used to calculate data size
        NutOutputStream bufOutput = new NutOutputStream(buffer);

        // stream_id_plus1
        bufOutput.writeValue(info.streamId + 1);
        bufOutput.writeSignedValue(info.chapterId);
        Timestamp timestamp = new Timestamp(info.timebaseId, info.chapterStartPts);
        bufOutput.writeTimestamp(mainHeader.timeBases.length, timestamp);
        bufOutput.writeValue(info.chapterLengthPts);
        writeDataItems(info.metaData, bufOutput);

        bufOutput.flush();
        writePacket(NutConst.INFO_STARTCODE, buffer.toByteArray());
    }

    private void writeSyncPoint() throws IOException {
        long maxPts = lastPts[0];
        int maxI = 0;
        for (int i = 1; i < mainHeader.timeBases.length; i++) {
            long pts = Util.convertTimestamp(lastPts[i], mainHeader.timeBases[i], mainHeader.timeBases[maxI]);
            if (pts > maxPts) {
                maxPts = lastPts[i];
                maxI = i;
            }
        }
        Timestamp globalKeyPts = new Timestamp(maxI, maxPts);
        long backPtr = (output.getPosition() - lastSyncPointPosition) / 16;
        SyncPoint syncPoint = new SyncPoint(globalKeyPts, backPtr);

        for (int i = 0; i < mainHeader.timeBases.length; i++) {
            if (i == maxI) {
                continue;
            }
            long pts = Util.convertTimestamp(maxPts, mainHeader.timeBases[maxI], mainHeader.timeBases[i]);
            lastPts[i] = pts;
        }

        buffer.reset();
        // Temp buffer, used to calculate data size
        NutOutputStream bufOutput = new NutOutputStream(buffer);

        bufOutput.writeTimestamp(mainHeader.timeBases.length, syncPoint.globalKeyPts);
        bufOutput.writeValue(syncPoint.backPtrDiv16);
        if (mainHeader.flags.contains(MainHeader.Flag.BROADCAST_MODE)) {
            bufOutput.writeTimestamp(mainHeader.timeBases.length, syncPoint.transmitTs);
        }
        lastSyncPointPosition = output.getPosition();

        bufOutput.flush();
        writePacket(NutConst.SYNCPOINT_STARTCODE, buffer.toByteArray());
    }

    private void writeDataItems(DataItem[] items, NutOutputStream output) throws IOException {
        if (items == null) {
            items = new DataItem[0];
        }
        output.writeValue(items.length);

        for (DataItem item : items) {
            output.writeVariablesString(item.name);

            switch (item.type) {
                case "UTF-8":
                    output.writeSignedValue(-1);
                    output.writeVariablesString((String) item.value);
                    break;
                case "s":
                    output.writeSignedValue(-3);
                    output.writeSignedValue(((Number) item.value).longValue());
                    break;
                case "t":
                    output.writeSignedValue(-4);
                    output.writeTimestamp(mainHeader.timeBases.length, (Timestamp) item.value);
                    break;
                case "r":
                    Rational rational = (Rational) item.value;
                    output.writeSignedValue(-rational.denominator - 4);
                    output.writeSignedValue(rational.numerator);
                    break;
                case "v":
                    long value = ((Number) item.value).longValue();
                    if (value < 0) {
                        throw new IllegalArgumentException("Value with type 'v' must be non negative");
                    }
                    output.writeSignedValue(value);
                default:
                    output.writeSignedValue(-2);
                    output.writeVariablesString(item.type);
                    output.writeVariableBytes((byte[]) item.value);
            }
        }
    }


    private void writePacket(long startcode, byte[] data) throws IOException {
        long forwardPtr = data.length + 4; // checksum footer it 4 bytes long
        output.resetCrc32();

        output.writeLong(startcode);
        output.writeValue(forwardPtr);

        if (forwardPtr > 4096) {
            output.writeCrc32();
        }

        output.resetCrc32();
        output.writeBytes(data);
        output.writeCrc32();
        output.flush();
    }


    private static class TsFrame {
        public final Rational timestamp;
        public final NutFrame frame;

        private static final Comparator<TsFrame> COMPARATOR = new Comparator<TsFrame>() {
            @Override
            public int compare(TsFrame o1, TsFrame o2) {
                return o1.timestamp.compareTo(o2.timestamp);
            }
        };

        public TsFrame(Rational timestamp, NutFrame frame) {
            this.timestamp = timestamp;
            this.frame = frame;
        }
    }
}
