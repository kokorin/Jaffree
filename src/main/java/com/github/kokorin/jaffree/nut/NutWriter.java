package com.github.kokorin.jaffree.nut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public class NutWriter {
    private final NutOutputStream output;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private MainHeader mainHeader;
    private StreamHeader[] streamHeaders;
    private Info[] infos;

    private boolean initialized = false;

    private static final long MAJOR_VERSION = 3;
    private static final long MINOR_VERSION = 0;

    public NutWriter(NutOutputStream output) {
        this.output = output;
    }

    public void setMainHeader(MainHeader mainHeader) {
        this.mainHeader = mainHeader;
    }

    public void setStreamHeaders(StreamHeader[] streamHeaders) {
        this.streamHeaders = streamHeaders;
    }

    public void setInfos(Info[] infos) {
        this.infos = infos;
    }

    private void initialize() throws IOException {
        if (initialized) {
            return;
        }

        initialized = true;
    }

    private void writeMainHeader() throws IOException {
        if (mainHeader == null) {
            throw new IllegalArgumentException("MainHeader must be specified before");
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
        Set<FrameTable.Flag> flags;

        for (int i = 0; i < 256; ) {
            fields = 0;
            FrameTable frameTable = mainHeader.frameTables[i];
            flags = frameTable.flags;

            if (frameTable.ptsDelta != ptsDelta) {
                fields = 1;
            }
            ptsDelta = frameTable.ptsDelta;

            if (frameTable.dataSizeMul != dataSizeMul) {
                fields = 2;
            }
            dataSizeMul = frameTable.dataSizeMul;

            if (frameTable.streamId != streamId) {
                fields = 3;
            }
            streamId = frameTable.streamId;

            if (frameTable.dataSizeLsb != 0) {
                fields = 4;
            }
            size = frameTable.dataSizeLsb;

            int count;
            for (count = 0; i < 256; count++, i++) {
                if (i == 'N') {
                    count--;
                    continue;
                }

                frameTable = mainHeader.frameTables[i];
                boolean flagsAreEqual = frameTable.flags.containsAll(flags) && frameTable.flags.size() == flags.size();
                if (!flagsAreEqual) {
                    break;
                }
                if (frameTable.streamId != streamId) {
                    break;
                }
                if (frameTable.dataSizeMul != dataSizeMul) {
                    break;
                }
                if (frameTable.dataSizeLsb != size + count) {
                    break;
                }
                if (frameTable.ptsDelta != ptsDelta) {
                    break;
                }
            }

            if (count != dataSizeMul - size) {
                fields = 6;
            }

            bufOutput.writeValue(FrameTable.Flag.toBitCode(flags));
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

        writePacket(NutConst.STREAM_STARTCODE, buffer.toByteArray());
    }

    public void writeFrame(NutFrame frame) throws IOException {

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

        writePacket(NutConst.INFO_STARTCODE, buffer.toByteArray());
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

        output.writeValue(startcode);
        output.writeValue(forwardPtr);

        if (forwardPtr > 4096) {
            output.writeCrc32();
        }

        output.resetCrc32();
        output.writeBytes(data);
        output.writeCrc32();
    }
}
