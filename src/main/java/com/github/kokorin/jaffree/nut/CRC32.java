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

/**
 * CRC32 implementation used in NUT.
 * <p>
 * Generator polynomial is 0x104C11DB7. Starting value is zero.
 * <p>
 * Can't be replaced with java.util.zip.CRC32 because it has different polynomial generator
 */
@SuppressWarnings("checkstyle:MagicNumber")
class CRC32 {
    private int crc = 0;

    private static final int[] TABLE = {
            0x00000000, 0x04C11DB7, 0x09823B6E, 0x0D4326D9,
            0x130476DC, 0x17C56B6B, 0x1A864DB2, 0x1E475005,
            0x2608EDB8, 0x22C9F00F, 0x2F8AD6D6, 0x2B4BCB61,
            0x350C9B64, 0x31CD86D3, 0x3C8EA00A, 0x384FBDBD,
    };

    public void reset() {
        crc = 0;
    }

    public void update(final int value) {
        crc ^= value << 24;
        crc = (crc << 4) ^ TABLE[crc >>> 28];
        crc = (crc << 4) ^ TABLE[crc >>> 28];
    }

    public void update(final byte[] data) {
        for (int value : data) {
            update(value);
        }
    }

    public long getValue() {
        return (long) crc & 0xffffffffL;
    }
}
