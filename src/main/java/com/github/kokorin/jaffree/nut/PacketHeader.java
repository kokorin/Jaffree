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

public class PacketHeader {
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
