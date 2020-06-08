package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.OS;

/**
 * Most of the information comes from https://trac.ffmpeg.org/wiki/Capture/Desktop
 *
 * TODO list:
 * - Screen selection when multiscreen?
 * - Area selection
 * - Audio Capture?
 * - Warn if framerate is not set (like FrameInput)
 * - Call ffmpeg to return list of devices? list of screens?
 */
public class DesktopCaptureInput extends BaseInput<DesktopCaptureInput> implements Input {
    private final boolean WINDOWS_USE_GDI = true;

    public DesktopCaptureInput(String screen) {
        if (OS.IS_LINUX) {
            setFormat("x11grab");
            setInput(":0.0");
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
