/*
 *    Copyright  2019 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.OS;

import java.awt.*;

/**
 * Most of the information comes from https://trac.ffmpeg.org/wiki/Capture/Desktop
 * <p>
 * TODO list:
 * - Screen selection when multiscreen?
 * - Audio Capture?
 * - Warn if framerate is not set (like FrameInput)
 * - Call ffmpeg to return list of devices? list of screens?
 */
public class DesktopCaptureInput extends BaseInput<DesktopCaptureInput> implements Input {
    private final boolean WINDOWS_USE_GDI = true;
    private String input = "";

    public DesktopCaptureInput(String screen) {
        if (OS.IS_LINUX) {
            setFormat("x11grab");
            input = ":0.0";
            setInput(input);
        }
        else if (OS.IS_MAC) {
            // Device list can be obtained with ffmpeg -f avfoundation -list_devices true -i ""
            setFormat("avfoundation");
            setInput("default:none");
            // For audio: setInput("default:default");
        }
        else if (OS.IS_WINDOWS) {
            if (WINDOWS_USE_GDI) {
                setFormat("gdigrab");
                setInput("desktop");
            }
            else {
                // Using DirectShow
                // Device list can be obtained with ffmpeg -f dshow -list_devices true -i ""
                setFormat("dshow");
                setInput("video=\"screen-capture-recorder\"");
                // For audio: setInput("video=\"screen-capture-recorder\":audio=\"virtual-audio-capturer\"");
            }
        }
    }

    public DesktopCaptureInput setArea(Rectangle area) {
        if (area != null) {
            if (OS.IS_LINUX) {
                // Specific way to select area with avfoundation
                addArguments("-video_size", area.width + "x" + area.height);
                setInput(input + "+" + area.x + "," + area.y);
                return this;
            }
            else if (OS.IS_WINDOWS && WINDOWS_USE_GDI) {
                // Specific way to select area with gdigrab
                addArguments("-video_size", area.width + "x" + area.height);
                addArguments("-offset_x", String.valueOf(area.x));
                addArguments("-offset_y", String.valueOf(area.x));
            }
            else {
                // Generic way to crop after capture
                // TODO is this the right way to do ?
                // addArguments("-vf", "\"crop=" + area.width + ":" + area.height + ":" + area.x + ":" + area.y + "\"");
                // TODO This doesn't seem to work as filter should come after input.
                // TODO Plus, having the filter part of the input seems wrong, but what else can we do ?
                // TODO Or throw an exception and request that user adds a crop filter downstream in the chain ?
            }
        }
        return this;
    }

    @Override
    public DesktopCaptureInput setFrameRate(Number value) {
        return super.setFrameRate(value);
    }

    /**
     * Include mouse cursor (only works on Mac)
     *
     * @param includeCursor
     * @return
     */
    public DesktopCaptureInput includeMouseCursor(boolean includeCursor) {
        if (OS.IS_MAC && includeCursor) {
            addArguments("-capture_cursor", "1");
        }
        return this;
    }

    public static DesktopCaptureInput fromScreen() {
        return new DesktopCaptureInput(null);
    }

    public static DesktopCaptureInput fromScreen(String screen) {
        return new DesktopCaptureInput(screen);
    }
}
