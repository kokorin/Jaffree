/*
 *    Copyright 2021 Denis Kokorin
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

module com.github.kokorin.jaffree {
    requires java.base;
    requires java.desktop;
    requires org.slf4j;
    requires jcip.annotations;
    requires com.grack.nanojson;

    exports com.github.kokorin.jaffree;
    exports com.github.kokorin.jaffree.ffmpeg;
    exports com.github.kokorin.jaffree.ffprobe;
    exports com.github.kokorin.jaffree.ffprobe.data;
    exports com.github.kokorin.jaffree.log;
    exports com.github.kokorin.jaffree.net;
    exports com.github.kokorin.jaffree.nut;
    exports com.github.kokorin.jaffree.process;
    exports com.github.kokorin.jaffree.util;
}