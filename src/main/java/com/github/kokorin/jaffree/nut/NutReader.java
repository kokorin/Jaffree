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

import java.io.IOException;
import java.util.*;

public class NutReader {
    private final NutInputStream input;
    private boolean read = false;
    private MainHeader mainHeader;
    private StreamHeader[] streamHeaders;
    private Info[] infos;
    private long[] lastPts;

    public NutReader(NutInputStream input) {
        this.input = input;
    }

    public MainHeader getMainHeader() throws IOException {
        readToFrame();
        return mainHeader;
    }

    public StreamHeader[] getStreamHeaders() throws IOException {
        readToFrame();
        return Arrays.copyOf(streamHeaders, streamHeaders.length);
    }

    public Info[] getInfos() throws IOException {
        readToFrame();
        return Arrays.copyOf(infos, infos.length);
    }

    // package-private for tests
    private void readToFrame() throws IOException {
        if (input.getPosition() == 0) {
            String fileId = input.readCString();
            if (!Objects.equals(fileId, NutConst.FILE_ID)) {
                throw new JaffreeException("Wrong file ID: " + fileId);
            }
        }

        while (input.checkNextByte() == (byte) 'N') {
            PacketHeader packetHeader = readPacketHeader();
            long nextPacketPosition = input.getPosition() + packetHeader.forwardPtr;

            if (packetHeader.startcode == NutConst.MAIN_STARTCODE) {
                mainHeader = readMainHeader();
                if (streamHeaders == null) {
                    streamHeaders = new StreamHeader[mainHeader.streamCount];
                }
                if (infos == null) {
                    infos = new Info[mainHeader.streamCount];
                }
                if (lastPts == null) {
                    lastPts = new long[mainHeader.streamCount];
                }
            } else if (packetHeader.startcode == NutConst.STREAM_STARTCODE) {
                StreamHeader streamHeader = readStreamHeader();
                streamHeaders[streamHeader.streamId] = streamHeader;
            } else if (packetHeader.startcode == NutConst.INFO_STARTCODE) {
                Info info = readInfo();
                if (info.streamId >= 0) {
                    infos[info.streamId] = info;
                } else {
                    // Check description of stream_id_plus1 (v)
                    // Stream this info packet applies to. If zero, packet applies to all streams.
                    // But we have normalized streamId while reading Info to range -1..streamCount-1
                    for (int i = 0; i < mainHeader.streamCount; i++) {
                        infos[i] = info;
                    }
                }
            } else if (packetHeader.startcode == NutConst.SYNCPOINT_STARTCODE) {
                SyncPoint syncPoint = readSyncPoint();
                long pts = syncPoint.globalKeyPts.pts;
                Rational ptsTimebase = mainHeader.timeBases[syncPoint.globalKeyPts.timebaseId];
                for (int i = 0; i < mainHeader.timeBases.length; i++) {
                    lastPts[i] = Util.convertTimestamp(pts, ptsTimebase, mainHeader.timeBases[i]);
                }
            }

            // Intentionally ignore these headers: INDEX (and reserved headers also)

            input.skipBytes(nextPacketPosition - input.getPosition() - 4);
            readPacketFooter();
        }
    }

    /*
        packet_header
        startcode                           f(64)
        forward_ptr                         v
        if(forward_ptr > 4096)
            header_checksum                 u(32)
     */
    private PacketHeader readPacketHeader() throws IOException {
        long startcode = input.readLong();
        long forwardPtr = input.readValue();
        long headerChecksum = 0;
        if (forwardPtr > 4096) {
            headerChecksum = input.readInt();
        }

        return new PacketHeader(startcode, forwardPtr, headerChecksum);
    }

    private MainHeader readMainHeader() throws IOException {
        long majorVersion = input.readValue();
        long minorVersion = 0;
        if (majorVersion > 3) {
            minorVersion = input.readValue();
        }

        int streamCount = (int) input.readValue();
        long maxDistance = input.readValue();
        int timeBaseCount = (int) input.readValue();

        Rational[] timeBases = new Rational[timeBaseCount];
        for (int i = 0; i < timeBaseCount; i++) {
            long numerator = input.readValue();
            long denominator = input.readValue();
            timeBases[i] = new Rational(numerator, denominator);
        }

        Set<FrameCode.Flag> flags;
        int streamId = 0, dataSizeMul = 1, size;
        long fields, ptsDelta = 0, reserved, count, matchTimeDelta = 1L - (1L << 62), elisionHeaderIdx = 0;
        FrameCode[] frameCodes = new FrameCode[256];
        for (int i = 0; i < 256; ) {
            flags = FrameCode.Flag.fromBitCode(input.readValue());
            fields = input.readValue();

            if (fields > 0) {
                ptsDelta = input.readSignedValue();
            }
            if (fields > 1) {
                dataSizeMul = (int) input.readValue();
            }
            if (fields > 2) {
                streamId = (int) input.readValue();
            }
            if (fields > 3) {
                size = (int) input.readValue();
            } else {
                size = 0;
            }
            if (fields > 4) {
                reserved = input.readValue();
            } else {
                reserved = 0;
            }
            if (fields > 5) {
                count = input.readValue();
            } else {
                count = dataSizeMul - size;
            }

            // MatchTimeDelta is present in NUT specification, but is absent in FFMPEG NUT implementation
            if (fields > 6) {
                matchTimeDelta = input.readSignedValue();
            }
            // ElisionHeaders are present in NUT specification, but are absent in FFMPEG NUT implementation
            if (fields > 7) {
                elisionHeaderIdx = input.readValue();
            }
            for (int j = 8; j < fields; j++) {
                input.readValue(); //ignore unknown fields
            }

            for (int j = 0; j < count && i < 256; j++, i++) {
                final FrameCode ft;

                if (i == 'N') {
                    ft = FrameCode.INVALID;
                    j--;
                } else {
                    ft = new FrameCode(flags, streamId, dataSizeMul, size + j, ptsDelta, reserved, matchTimeDelta, elisionHeaderIdx);
                }

                frameCodes[i] = ft;
            }
        }

        // ElisionHeaders are present in NUT specification, but are absent in FFMPEG NUT implementation
        // int elisionHeaderCount = (int) input.readValue();
        // long[] elisionHeaderSize = new long[elisionHeaderCount];
        // for (int i = 0; i < elisionHeaderCount; i++) {
        //    elisionHeaders[i] = input.readVariableBytes().length;
        // }
        long[] elisionHeaderSize = new long[255];

        // <MainHeader.Flag is present in NUT specification, but is absent in FFMPEG NUT implementation
        // Set<MainHeader.Flag> mainFlags = MainHeader.Flag.fromBitCode(input.readValue());
        Set<MainHeader.Flag> mainFlags = Collections.emptySet();

        return new MainHeader(majorVersion, minorVersion, streamCount, maxDistance, timeBases, frameCodes, elisionHeaderSize, mainFlags);
    }

    private StreamHeader readStreamHeader() throws IOException {
        int streamId = (int) input.readValue();
        StreamHeader.Type streamType = StreamHeader.Type.fromCode(input.readValue());
        byte[] fourcc = input.readVariableBytes();
        int timeBaseId = (int) input.readValue();
        int msbPtsShift = (int) input.readValue();
        long maxPtsDistance = input.readValue();
        long decodeDelay = input.readValue();
        Set<StreamHeader.Flag> flags = StreamHeader.Flag.fromBitCode(input.readValue());
        byte[] codecSpcificData = input.readVariableBytes();

        StreamHeader.Video video = null;
        StreamHeader.Audio audio = null;
        if (streamType == StreamHeader.Type.VIDEO) {
            int width = (int) input.readValue();
            int height = (int) input.readValue();
            int sampleWidth = (int) input.readValue();
            int sampleHeight = (int) input.readValue();
            StreamHeader.ColourspaceType colourspaceType = StreamHeader.ColourspaceType.fromCode(input.readValue());

            video = new StreamHeader.Video(width, height, sampleWidth, sampleHeight, colourspaceType);
        } else if (streamType == StreamHeader.Type.AUDIO) {
            long samplerateNumerator = input.readValue();
            long samplerateDenominator = input.readValue();
            int channelCount = (int) input.readValue();

            Rational sampleRate = new Rational(samplerateNumerator, samplerateDenominator);
            audio = new StreamHeader.Audio(sampleRate, channelCount);
        }

        return new StreamHeader(streamId, streamType, fourcc, timeBaseId, msbPtsShift, maxPtsDistance, decodeDelay,
                flags, codecSpcificData, video, audio);
    }

    private SyncPoint readSyncPoint() throws IOException {
        Timestamp pts = input.readTimestamp(mainHeader.timeBases.length);
        long backPtrDiv16 = input.readValue();
        Timestamp transmitTs = pts;
        if (mainHeader.flags.contains(MainHeader.Flag.BROADCAST_MODE)) {
            transmitTs = input.readTimestamp(mainHeader.timeBases.length);
        }

        return new SyncPoint(pts, backPtrDiv16, transmitTs);
    }

    public NutFrame readFrame() throws IOException {
        readToFrame();

        if (!input.hasMoreData()) {
            return null;
        }

        int frameCode = input.readByte();
        FrameCode frameTable = mainHeader.frameCodes[frameCode];

        Set<FrameCode.Flag> flags = frameTable.flags;
        int streamId = frameTable.streamId;
        final StreamHeader streamHeader;
        final long pts;
        long dataSizeMsb = 0;
        long dataSizeMul = frameTable.dataSizeMul;
        long dataSizeLsb = frameTable.dataSizeLsb;
        long reservedValues = frameTable.reservedCount;
        long matchTimeDelta = frameTable.matchTimeDelta;
        long elisionHeaderSize = 0;
        DataItem[] sideData = null;
        DataItem[] metaData = null;

        if (flags.contains(FrameCode.Flag.CODED_FLAGS)) {
            flags = EnumSet.copyOf(flags);
            Set<FrameCode.Flag> codedFlags = FrameCode.Flag.fromBitCode(input.readValue());
            // flags = flags XOR codedFlags
            flags = FrameCode.Flag.xor(flags, codedFlags);
        }

        if (flags.contains(FrameCode.Flag.STREAM_ID)) {
            streamId = (int) input.readValue();
        }
        streamHeader = streamHeaders[streamId];

        if (flags.contains(FrameCode.Flag.CODED_PTS)) {
            /*
            If coded_pts < ( 1 << msb_pts_shift ) then it is an lsb
            pts, otherwise it is a full pts + ( 1 << msb_pts_shift ).
            lsb pts is converted to a full pts by:
            mask  = ( 1 << msb_pts_shift ) - 1;
            delta = last_pts - mask / 2
            pts   = ( (pts_lsb - delta) & mask ) + delta
             */
            long codedPts = input.readValue();
            int shift = streamHeader.msbPtsShift;
            if (Util.compareUnsigned(codedPts, 1L << shift) >= 0) {
                pts = codedPts - (1L << shift);
            } else {
                long mask = (1L << shift) - 1;
                long delta = lastPts[streamId] - mask / 2;
                pts = ((codedPts - delta) & mask) + delta;
            }
        } else {
            // pd->pts = nut->sc[pd->stream].last_pts + nut->ft[tmp].pts_delta;
            pts = lastPts[streamId] + frameTable.ptsDelta;
        }

        if (flags.contains(FrameCode.Flag.SIZE_MSB)) {
            dataSizeMsb = input.readValue();
        }

        // MatchTimeDelta is present in NUT specification, but is absent in FFMPEG NUT implementation
        if (flags.contains(FrameCode.Flag.MATCH_TIME)) {
            matchTimeDelta = input.readSignedValue();
        }

        // ElisionHeaders are present in NUT specification, but are absent in FFMPEG NUT implementation
        if (flags.contains(FrameCode.Flag.HEADER_IDX)) {
            int elisionHeaderIdx = (int) input.readValue();
            elisionHeaderSize = mainHeader.elisionHeaderSize[elisionHeaderIdx];
        }

        if (flags.contains(FrameCode.Flag.RESERVED)) {
            reservedValues = input.readValue();
        }

        for (int i = 0; i < reservedValues; i++) {
            input.readValue(); // ignore reserved
        }

        // checksum is ignored
        if (flags.contains(FrameCode.Flag.CHECKSUM)) {
            long checksum = input.readInt();
        }

        if (flags.contains(FrameCode.Flag.SM_DATA)) {
            sideData = readDataItems();
            metaData = readDataItems();
        }

        /*
            data_size = data_size_lsb + data_size_msb * data_size_mul ;
            The size of the following frame, including a possible elision header.
            If data_size is 500 bytes, and it has an elision header of 10 bytes,
            then the stored frame data following the frame header is 490 bytes.
         */
        long dataSizeWithElision = dataSizeLsb + dataSizeMsb * dataSizeMul;
        long dataSize = dataSizeWithElision - elisionHeaderSize;

        byte[] data = input.readBytes(dataSize);
        input.skipBytes(elisionHeaderSize);
        boolean keyframe = flags.contains(FrameCode.Flag.KEYFRAME);
        boolean eor = flags.contains(FrameCode.Flag.EOR) || dataSize == 0;

        lastPts[streamId] = pts;
        return new NutFrame(streamId, pts, data, sideData, metaData, keyframe, eor);
    }

    private Info readInfo() throws IOException {
        // stream_id_plus1
        int streamId = (int) (input.readValue() - 1);
        int chapterId = (int) input.readSignedValue();

        Timestamp timestamp = input.readTimestamp(mainHeader.timeBases.length);

        long chapterStartPts = timestamp.pts;
        long chapterLengthPts = input.readValue();
        DataItem[] meta = readDataItems();

        return new Info(streamId, chapterId, chapterStartPts, chapterLengthPts, timestamp.timebaseId, meta);
    }

    private DataItem[] readDataItems() throws IOException {
        int count = (int) input.readValue();
        DataItem[] result = new DataItem[count];

        for (int i = 0; i < count; i++) {
            String name = input.readVariableString();
            long valueCode = input.readSignedValue();
            final String type;
            final Object value;

            if (valueCode == -1) {
                type = "UTF-8";
                value = input.readVariableString();
            } else if (valueCode == -2) {
                type = input.readVariableString();
                value = input.readVariableBytes();
            } else if (valueCode == -3) {
                type = "s";
                value = input.readSignedValue();
            } else if (valueCode == -4) {
                type = "t";
                value = input.readTimestamp(mainHeader.timeBases.length);
            } else if (valueCode < -4) {
                type = "r";
                long denominator = -valueCode - 4;
                long numerator = input.readSignedValue();
                value = new Rational(numerator, denominator);
            } else {
                type = "v";
                value = valueCode;
            }

            result[i] = new DataItem(name, value, type);
        }

        return result;
    }

    /*
        packet_footer
        headerChecksum                            u(32)
     */
    private PacketFooter readPacketFooter() throws IOException {
        long checksum = input.readInt();
        return new PacketFooter(checksum);
    }


    private static class PacketHeader {
        public final long startcode;

        /**
         * Size of the packet data (exactly the distance from the first byte
         * after the packet_header to the first byte of the next packet).
         * <p>
         * Every NUT packet contains a forward_ptr immediately after its startcode
         * with the exception of frame_code-based packets. The forward pointer
         * can be used to skip over the packet without decoding its contents.
         */
        public final long forwardPtr;
        public final long headerChecksum;

        public PacketHeader(long startcode, long forwardPtr, long headerChecksum) {
            this.startcode = startcode;
            this.forwardPtr = forwardPtr;
            this.headerChecksum = headerChecksum;
        }
    }

    private static class PacketFooter {
        public final long checksum;

        public PacketFooter(long checksum) {
            this.checksum = checksum;
        }
    }
}
