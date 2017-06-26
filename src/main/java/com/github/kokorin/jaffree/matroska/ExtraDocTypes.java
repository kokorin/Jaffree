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

package com.github.kokorin.jaffree.matroska;


import org.ebml.MasterElement;
import org.ebml.ProtoType;

public class ExtraDocTypes {
    // FFMPEG creates MKV files with Colour element. It seems that Colour is present in Matroska's 4th version only.
    // Without such initialization, an exception will occur while parsing MKV.
    public static final ProtoType<MasterElement> Colour = new ProtoType<>(MasterElement.class, "Colour", new byte[] {(byte) 0x55, (byte) 0xB0}, 4);


    private ExtraDocTypes() {}

    /**
     * Use this method to initialize this class before using JEBML
     */
    public static void init() {}
}
