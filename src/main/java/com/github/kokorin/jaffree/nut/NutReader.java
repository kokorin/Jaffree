package com.github.kokorin.jaffree.nut;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NutReader {
    private final InputStream input;
    private long position = 0;
    private boolean read = false;
    private MainHeader mainHeader;

    public NutReader(InputStream input) {
        if (!(input instanceof BufferedInputStream)) {
            input = new BufferedInputStream(input);
        }

        this.input = input;
    }

    MainHeader getMainHeader() throws Exception {
        readIfRequired();
        return mainHeader;
    }


    // package-private for tests
    void readIfRequired() throws Exception {
        if (read) {
            return;
        }

        for (byte b : NutConst.FILE_ID_BYTES) {
            if (b != input.read()) {
                throw new RuntimeException("Wrong file id");
            }
        }

        PacketHeader packetHeader = readPacketHeader();
        long mainHeaderStartPosition = position;
        mainHeader = readMainHeader();
        long mainHeaderNonReserveEndPosition = position;
        long nonReservedRead = mainHeaderNonReserveEndPosition - mainHeaderStartPosition;
        skipBytes(packetHeader.forwardPtr - nonReservedRead);
        readPacketFooter();

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
        long startcode = readLong();
        long forwardPtr = readValue();
        long headerChecksum = 0;
        if (forwardPtr > 4096) {
            headerChecksum = readInt();
        }

        return new PacketHeader(startcode, forwardPtr, headerChecksum);
    }

    private MainHeader readMainHeader() throws Exception {
        long majorVersion = readValue();
        long minorVersion = 0;
        if (majorVersion > 3) {
            minorVersion = readValue();
        }

        long streamCount = readValue();
        long maxDistance = readValue();
        long timeBaseCount = readValue();

        List<Rational> timeBases = new ArrayList<>();
        for (int i = 0; i < timeBaseCount; i++) {
            long numerator = readValue();
            long denominator = readValue();
            timeBases.add(new Rational(numerator, denominator));
        }

        Set<FrameTable.Flag> flags;
        long fields, ptsDelta = 0, dataSizeMul = 1, streamId = 0, size, reserved, count, match = 1L - (1L << 62), headIdx = 0;
        List<FrameTable> frameTables = new ArrayList<>(255);
        for (int i = 0; i < 256; ) {
            flags = FrameTable.Flag.fromBitCode(readValue());
            fields = readValue();

            if (fields > 0) {
                ptsDelta = readSigndValue();
            }
            if (fields > 1) {
                dataSizeMul = readValue();
            }
            if (fields > 2) {
                streamId = readValue();
            }
            if (fields > 3) {
                size = readValue();
            } else {
                size = 0;
            }
            if (fields > 4) {
                reserved = readValue();
            } else {
                reserved = 0;
            }
            if (fields > 5) {
                count = readValue();
            } else {
                count = dataSizeMul - size;
            }
            if (fields > 6) {
                match = readSigndValue();
            }
            if (fields > 7) {
                headIdx = readValue();
            }
            for (int j = 8; j < fields; j++) {
                readValue(); //ignore unknown fields
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

        int elisionHeaderCount = (int) readValue();
        List<String> elisionHeaders = new ArrayList<>(elisionHeaderCount);
        for (int i = 0; i < elisionHeaderCount; i++) {
            elisionHeaders.add(readString());
        }
        Set<MainHeader.Flag> mainFlags = MainHeader.Flag.fromBitCode(readValue());

        return new MainHeader(majorVersion, minorVersion, streamCount, maxDistance, timeBases, frameTables, elisionHeaders);
    }

    /*
        packet_footer
        headerChecksum                            u(32)
     */
    private PacketFooter readPacketFooter() throws Exception {
        long checksum = readInt();
        return new PacketFooter(checksum);
    }


    //reserved_headers

    /**
     * v   (variable length value, unsigned)
     *
     * @return unsigned value
     */
    long readValue() throws Exception {
        long result = 0;

        while (input.available() > 0) {
            int tmp = input.read();
            position++;

            boolean hasMore = (tmp & 0x80) > 0;
            if (hasMore)
                result = (result << 7) + tmp - 0x80;
            else
                return (result << 7) + tmp;
        }

        return -1;
    }

    /**
     * s   (variable length value, signed)
     *
     * @return signed value
     */
    long readSigndValue() throws Exception {
        long tmp = readValue();
        tmp++;
        if ((tmp & 1) > 0) {
            return -(tmp >> 1);
        }

        return tmp >> 1;
    }

    /**
     * f(n)    (n fixed bits in big-endian order)
     * n == 64
     *
     * @return long
     */
    long readLong() throws Exception {
        long result = 0;

        for (int i = 0; i < 8; i++) {
            result = (result << 8) + input.read();
            position++;
        }

        return result;
    }

    /**
     * u(n)    (unsigned number encoded in n bits in MSB-first order)
     * n == 32
     *
     * @return int as long
     */
    long readInt() throws Exception {
        long result = 0;

        for (int i = 0; i < 4; i++) {
            result = (result << 8) + input.read();
            position++;
        }

        return result;
    }

    /**
     * vb  (variable length binary data or string)
     *
     * @return String
     */
    String readString() throws Exception {
        byte[] bytes = readBytes();
        char[] result = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            result[i] = (char) bytes[i];
        }

        return String.valueOf(result);
    }

    /**
     * vb  (variable length binary data or string)
     *
     * @return String
     */
    byte[] readBytes() throws Exception {
        int length = (int) readValue();
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            result[i] = (byte) input.read();
            position++;
        }

        return result;
    }

    void skipBytes(long toSkip) throws Exception {
        while (toSkip > 0) {
            long skipped = input.skip(toSkip);
            position += skipped;
            toSkip -= skipped;
        }
    }

}
