package com.github.kokorin.jaffree.nut;

import java.util.*;

public class NutReader {
    private final NutInputStream input;
    private boolean read = false;
    private MainHeader mainHeader;
    private List<StreamHeader> streamHeaders;

    public NutReader(NutInputStream input) {
        this.input = input;
    }

    MainHeader getMainHeader() throws Exception {
        readIfRequired();
        return mainHeader;
    }

    List<StreamHeader> getStreamHeaders() throws Exception {
        readIfRequired();
        return streamHeaders;
    }

    // package-private for tests
    void readIfRequired() throws Exception {
        if (read) {
            return;
        }

        String fileId = input.readCString();
        if (!Objects.equals(fileId, NutConst.FILE_ID)) {
            throw new RuntimeException("Wrong file ID: " + fileId);
        }

        PacketHeader packetHeader = readPacketHeader();
        if (packetHeader.startcode != NutConst.MAIN_STARTCODE) {
            throw new RuntimeException("Unexpected startcode: " + Long.toHexString(packetHeader.startcode));
        }

        long nextPacketPosition = input.getPosition() + packetHeader.forwardPtr;
        mainHeader = readMainHeader();
        input.skipBytes(nextPacketPosition - input.getPosition() - 4);
        readPacketFooter();

        streamHeaders = new ArrayList<>((int) mainHeader.streamCount);
        for (int i = 0; i < mainHeader.streamCount; i++) {
            PacketHeader streamPacketHeader = readPacketHeader();
            if (streamPacketHeader.startcode != NutConst.STREAM_STARTCODE) {
                throw new RuntimeException("Unexpected startcode: " + Long.toHexString(packetHeader.startcode));
            }

            nextPacketPosition = input.getPosition() + streamPacketHeader.forwardPtr;
            StreamHeader streamHeader = readStreamHeader();
            input.skipBytes(nextPacketPosition - input.getPosition() - 4);
            streamHeaders.add(streamHeader);
            readPacketFooter();
        }

        read = true;
    }

    /*
        packet_header
        startcode                           f(64)
        forward_ptr                         v
        if(forward_ptr > 4096)
            header_checksum                 u(32)
     */
    private PacketHeader readPacketHeader() throws Exception {
        long startcode = input.readLong();
        long forwardPtr = input.readValue();
        long headerChecksum = 0;
        if (forwardPtr > 4096) {
            headerChecksum = input.readInt();
        }

        return new PacketHeader(startcode, forwardPtr, headerChecksum);
    }

    private MainHeader readMainHeader() throws Exception {
        long majorVersion = input.readValue();
        long minorVersion = 0;
        if (majorVersion > 3) {
            minorVersion = input.readValue();
        }

        long streamCount = input.readValue();
        long maxDistance = input.readValue();
        long timeBaseCount = input.readValue();

        List<Rational> timeBases = new ArrayList<>();
        for (int i = 0; i < timeBaseCount; i++) {
            long numerator = input.readValue();
            long denominator = input.readValue();
            timeBases.add(new Rational(numerator, denominator));
        }

        Set<FrameTable.Flag> flags;
        long fields, ptsDelta = 0, dataSizeMul = 1, streamId = 0, size, reserved, count, match = 1L - (1L << 62), headIdx = 0;
        List<FrameTable> frameTables = new ArrayList<>(255);
        for (int i = 0; i < 256; ) {
            flags = FrameTable.Flag.fromBitCode(input.readValue());
            fields = input.readValue();

            if (fields > 0) {
                ptsDelta = input.readSigndValue();
            }
            if (fields > 1) {
                dataSizeMul = input.readValue();
            }
            if (fields > 2) {
                streamId = input.readValue();
            }
            if (fields > 3) {
                size = input.readValue();
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
            if (fields > 6) {
                match = input.readSigndValue();
            }
            if (fields > 7) {
                headIdx = input.readValue();
            }
            for (int j = 8; j < fields; j++) {
                input.readValue(); //ignore unknown fields
            }

            for (int j = 0; j < count && i < 256; j++, i++) {
                final FrameTable ft;

                if (i == 'N') {
                    ft = FrameTable.INVALID;
                    j--;
                } else {
                    ft = new FrameTable(flags, streamId, dataSizeMul, size + j, ptsDelta, reserved, match, headIdx);
                }

                frameTables.add(ft);
            }
        }

        //int elisionHeaderCount = (int) input.readValue();
        List<String> elisionHeaders = new ArrayList<>();
        //for (int i = 0; i < elisionHeaderCount; i++) {
        //   elisionHeaders.add(input.readVariableString());
        //}
        //Set<MainHeader.Flag> mainFlags = MainHeader.Flag.fromBitCode(input.readValue());
        Set<MainHeader.Flag> mainFlags = Collections.emptySet();

        return new MainHeader(majorVersion, minorVersion, streamCount, maxDistance, timeBases, frameTables, elisionHeaders, mainFlags);
    }

    private StreamHeader readStreamHeader() throws Exception {
        long streamId = input.readValue();
        StreamHeader.Type streamType = StreamHeader.Type.fromCode(input.readValue());
        byte[] fourcc = input.readVariableBytes();
        long timeBaseId = input.readValue();
        long msbPtsShift = input.readValue();
        long maxPtsDistance = input.readValue();
        long decodeDelay = input.readValue();
        Set<StreamHeader.Flag> flags = StreamHeader.Flag.fromBitCode(input.readValue());
        byte[] codecSpcificData = input.readVariableBytes();

        StreamHeader.Video video = null;
        StreamHeader.Audio audio = null;
        if (streamType == StreamHeader.Type.VIDEO) {
            long width = input.readValue();
            long height = input.readValue();
            long sampleWidth = input.readValue();
            long sampleHeight = input.readValue();
            StreamHeader.ColourspaceType colourspaceType = StreamHeader.ColourspaceType.fromCode(input.readValue());

            video = new StreamHeader.Video(width, height, sampleWidth, sampleHeight, colourspaceType);
        } else if (streamType == StreamHeader.Type.AUDIO) {
            long samplerateNumerator = input.readValue();
            long samplerateDenominator = input.readValue();
            long channelCount = input.readValue();

            Rational sampleRate = new Rational(samplerateNumerator, samplerateDenominator);
            audio = new StreamHeader.Audio(sampleRate, channelCount);
        }

        return new StreamHeader(streamId, streamType, fourcc, timeBaseId, msbPtsShift, maxPtsDistance, decodeDelay, flags, video, audio);
    }

    private Frame readFrame() throws Exception {
        int frameCode = (int) input.readValue();
        FrameTable frameTable = mainHeader.frameTables.get(frameCode);

        Set<FrameTable.Flag> flags = frameTable.flags;
        long streamId = frameTable.streamId;
        long ptsDelta = frameTable.ptsDelta;
        long lsb = frameTable.dataSizeLsb;
        long reservedValues = frameTable.reservedCount;
        List<DataItem> sideData = Collections.emptyList();
        List<DataItem> metaData = Collections.emptyList();

        if(flags.contains(FrameTable.Flag.CODED)){
            flags = EnumSet.copyOf(flags);
            Set<FrameTable.Flag> codedFlags = FrameTable.Flag.fromBitCode(input.readValue());
            // flags = flags XOR codedFlags
            for (FrameTable.Flag codedFlag : codedFlags) {
                if (flags.contains(codedFlag)) {
                    flags.remove(codedFlag);
                } else {
                    flags.add(codedFlag);
                }
            }
        }

        if(flags.contains(FrameTable.Flag.STREAM_ID)){
            streamId = input.readValue();
        }

        if(flags.contains(FrameTable.Flag.CODED_PTS)){
            long codedPts = input.readValue();
            /*
            coded_pts (v)
            If coded_pts < ( 1 << msb_pts_shift ) then it is an lsb
            pts, otherwise it is a full pts + ( 1 << msb_pts_shift ).
            lsb pts is converted to a full pts by:
            mask  = ( 1 << msb_pts_shift ) - 1;
            delta = last_pts - mask / 2
            pts   = ( (pts_lsb - delta) & mask ) + delta
             */
        }

        if(flags.contains(FrameTable.Flag.SIZE_MSB)){
            long data_size_msb = input.readValue();
        }

        if(flags.contains(FrameTable.Flag.MATCH_TIME)){
            long match_time_delta = input.readSigndValue();
        }

        if(flags.contains(FrameTable.Flag.HEADER_IDX)){
            long header_idx = input.readValue();
        }

        if(flags.contains(FrameTable.Flag.RESERVED)) {
            reservedValues = input.readValue();
        }

        for(int i=0; i<reservedValues; i++) {
            input.readValue(); // ignore reserved
        }

        if(flags.contains(FrameTable.Flag.CHECKSUM)){
            long checksum = input.readInt();
        }

        if (flags.contains(FrameTable.Flag.SM_DATA)) {
            sideData = readDataItems();
            metaData = readDataItems();
        }

        // TODO read frame_data
        // frame_data

        return null;
    }

    private Info readInfo() throws Exception {
        // stream_id_plus1
        long streamId = input.readValue() - 1;
        long chapterId = input.readSigndValue();
        long chapterStart = readTimestamp();
        long chapterLength = input.readValue();
        List<DataItem> meta = readDataItems();

        return new Info(streamId, chapterId, chapterStart, chapterLength, meta);
    }

    private List<DataItem> readDataItems() throws Exception {
        long count = input.readValue();
        List<DataItem> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String name = input.readVariableString();
            long valueCode = input.readSigndValue();
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
                value = input.readSigndValue();
            } else if (valueCode == -4) {
                type = "t";
                value = readTimestamp();
            } else if (valueCode < -4) {
                type = "r";
                long denominator = -valueCode - 4;
                long numerator = input.readSigndValue();
                value = new Rational(numerator, denominator);
            } else {
                type = "v";
                value = valueCode;
            }

            result.add(new DataItem(name, value, type));
        }

        return result;
    }

    /*
        packet_footer
        headerChecksum                            u(32)
     */
    private PacketFooter readPacketFooter() throws Exception {
        long checksum = input.readInt();
        return new PacketFooter(checksum);
    }


    /*
        t (v coded universal timestamp)
     */
    private long readTimestamp() throws Exception {
        long tmp = input.readValue();
        int timeBaseCount = mainHeader.timeBases.size();
        int id = (int) (tmp % timeBaseCount);
        Rational timeBase = mainHeader.timeBases.get(id);

        return (tmp / timeBaseCount) * timeBase.numerator / timeBase.denominator;
    }

}
