package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.nut.FrameCode.Flag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class NutWriter implements AutoCloseable {
    private final NutOutputStream output;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private MainHeader mainHeader;
    private StreamHeader[] streamHeaders;
    private Info[] infos;
    private long[] lastPts;
    // true if last frame of the corresponding stream was EOR frame
    private boolean[] eor;

    private boolean initialized = false;

    private static final long MAJOR_VERSION = 3;
    private static final long MINOR_VERSION = 0;

    public NutWriter(NutOutputStream output) {
        this.output = output;
    }

    public void setMainHeader(MainHeader mainHeader) {
        if (initialized) {
            throw new RuntimeException("NutWriter is already initialized!");
        }
        this.mainHeader = mainHeader;
    }

    public void setStreamHeaders(StreamHeader[] streamHeaders) {
        if (initialized) {
            throw new RuntimeException("NutWriter is already initialized!");
        }
        this.streamHeaders = streamHeaders;
    }

    public void setInfos(Info[] infos) {
        if (initialized) {
            throw new RuntimeException("NutWriter is already initialized!");
        }
        this.infos = infos;
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
            throw new RuntimeException("StreamHeaders must be specified before");
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

        Timestamp firstSynpointTs = new Timestamp(0, 0);
        SyncPoint firstSyncponit = new SyncPoint(firstSynpointTs, 0);
        writeSyncPoint(firstSyncponit);

        initialized = true;
    }

    private void writeMainHeader() throws IOException {
        if (mainHeader == null) {
            throw new RuntimeException("MainHeader must be specified before");
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

    public void writeFrame(NutFrame fd) throws IOException {
        initialize();

        // TODO repetitions of Main and Stream Headers?
        // TODO handle EOR - End Of Relevance

        StreamHeader sc = streamHeaders[fd.streamId];

        int i, ftnum = -1, size = 0, msb_pts = (1 << sc.msbPtsShift);
        Set<Flag> codedFlags = Collections.emptySet();
        long coded_pts, pts_delta = fd.pts - lastPts[fd.streamId];
        boolean checksum = false;

        if (Math.abs(pts_delta) < (msb_pts / 2) - 1) {
            coded_pts = fd.pts & (msb_pts - 1);
        } else {
            coded_pts = fd.pts + msb_pts;
        }

        if (fd.data.length > 2 * mainHeader.maxDistance) {
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
            if (fd.keyframe) {
                fdFlags.add(Flag.KEYFRAME);
            }
            if (fd.eor) {
                fdFlags.add(Flag.EOR);
            }

            if (flags.contains(Flag.CODED_FLAGS)) {
                flags = EnumSet.copyOf(fdFlags);

                if (ft.streamId != fd.streamId) {
                    flags.add(Flag.STREAM_ID);
                }
                if (ft.ptsDelta != pts_delta) {
                    flags.add(Flag.CODED_PTS);
                }
                if (ft.dataSizeLsb != fd.data.length) {
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

            if (!flags.contains(Flag.STREAM_ID) && ft.streamId != fd.streamId) {
                continue;
            }

            if (!flags.contains(Flag.CODED_PTS) && ft.ptsDelta != pts_delta) {
                continue;
            }

            if (flags.contains(Flag.SIZE_MSB)) {
                if ((fd.data.length - ft.dataSizeLsb) % ft.dataSizeMul != 0) {
                    continue;
                }
            } else {
                if (ft.dataSizeLsb != fd.data.length) {
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
            throw new IllegalArgumentException("Can't find appropriate FrameCode for " + fd);
        }

        output.resetCrc32();
        output.writeByte(ftnum);
        FrameCode ft = mainHeader.frameCodes[ftnum];
        if (codedFlags.contains(Flag.CODED_FLAGS)) {
            Set<Flag> codedXor = Flag.xor(codedFlags, ft.flags);
            output.writeValue(Flag.toBitCode(codedXor));
        }
        if (codedFlags.contains(Flag.STREAM_ID)) {
            output.writeValue(fd.streamId);
        }
        if (codedFlags.contains(Flag.CODED_PTS)) {
            output.writeValue(coded_pts);
        }
        if (codedFlags.contains(Flag.SIZE_MSB)) {
            output.writeValue((fd.data.length - ft.dataSizeLsb) / ft.dataSizeMul);
        }
        if (codedFlags.contains(Flag.CHECKSUM)) {
            output.writeCrc32();
        }

        // TODO elision headers?
        output.writeBytes(fd.data);

        lastPts[fd.streamId] = fd.pts;
        eor[fd.streamId] = codedFlags.contains(Flag.EOR);

        if (eor[fd.streamId]) {
            System.out.printf("EOR!");
        }
    }

    @Override
    public void close() throws Exception {
        try (AutoCloseable toClose = output) {
            for (int streamId = 0; streamId < eor.length; streamId++) {
                if (!eor[streamId]) {
                    writeEorFrame(streamId);
                }
            }

            writeMainHeader();
            for (StreamHeader streamHeader : streamHeaders) {
                writeStreamHeader(streamHeader);
            }

        }
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

    private void writeSyncPoint(SyncPoint syncpoint) throws IOException {
        buffer.reset();
        // Temp buffer, used to calculate data size
        NutOutputStream bufOutput = new NutOutputStream(buffer);

        bufOutput.writeTimestamp(mainHeader.timeBases.length, syncpoint.globalKeyPts);
        bufOutput.writeValue(syncpoint.backKeyPts);
        if (syncpoint.transmitTs != null) {
            bufOutput.writeTimestamp(mainHeader.timeBases.length, syncpoint.transmitTs);
        }

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
}
