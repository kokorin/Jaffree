/*
 *    Copyright  2020-2021 Vicne, Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg.input;

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Input provides a live capture of your computer desktop as source.
 * <p>
 * Most of the information comes from <a href="https://trac.ffmpeg.org/wiki/Capture/Desktop">ffmpeg
 * capture desktop documentation</a>
 * <p>
 * TODO list:
 * - Screen selection when multiscreen?
 * - Audio Capture?
 * - Call ffmpeg to return list of devices? list of screens?
 *
 * @param <T> self
 */
public abstract class CaptureInput<T extends CaptureInput<T>> extends BaseInput<T>
        implements Input {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureInput.class);

    /**
     * @param input input identifier
     */
    protected CaptureInput(final String input) {
        super(input);
    }

    /**
     * Set capture frame rate.
     * <p>
     * Captures the desktop at the given frame rate
     * <p>
     * Be careful <b>not</b> to specify framerate with the "-r" parameter,
     * like this "ffmpeg -f dshow -r 7.5 -i video=XXX". This actually specifies that the devices
     * incoming PTS timestamps be <b>ignored</b> and replaced as if the device were running
     * at 7.5 fps [so it runs at default fps, but its timestamps are treated as if 7.5 fps].
     * This can cause the recording to appear to have "video slower than audio" or, under high
     * cpu load (if video frames are dropped) it will cause the video to fall "behind" the audio
     * [after playback of the recording is done, audio continues on--and gets highly out of sync,
     * video appears to go into "fast forward" mode during high cpu scenes].
     *
     * @param value Hz value, fraction or abbreviation
     * @return this
     */
    public CaptureInput<T> setCaptureFrameRate(final Number value) {
        return addArguments("-framerate", String.valueOf(value));
    }

    /**
     * Be careful <b>not</b> to specify framerate with the "-r" parameter,
     * like this "ffmpeg -f dshow -r 7.5 -i video=XXX". This actually specifies that the devices
     * incoming PTS timestamps be <b>ignored</b> and replaced as if the device were running
     * at 7.5 fps [so it runs at default fps, but its timestamps are treated as if 7.5 fps].
     * This can cause the recording to appear to have "video slower than audio" or, under high
     * cpu load (if video frames are dropped) it will cause the video to fall "behind" the audio
     * [after playback of the recording is done, audio continues on--and gets highly out of sync,
     * video appears to go into "fast forward" mode during high cpu scenes].
     *
     * @param streamSpecifier stream specifier
     * @param value           Hz value, fraction or abbreviation
     * @return this
     * @see #setCaptureFrameRate(Number)
     */
    @Override
    public T setFrameRate(final String streamSpecifier, final Number value) {
        LOGGER.warn("Be careful not to specify framerate with the \"-r\" parameter, rather use "
                + "setCaptureFrameRate method or \"-framerate\" parameter");
        return super.setFrameRate(streamSpecifier, value);
    }

    /**
     * Set the video size in the captured video.
     *
     * @param width  width
     * @param height height
     * @return this
     */
    public CaptureInput<T> setCaptureVideoSize(final int width, final int height) {
        return setCaptureVideoSize(width + "x" + height);
    }

    /**
     * Sets the video size in the captured video given as a {@link String} such as 640x480 or hd720.
     *
     * @param size video size
     * @return this
     */
    public CaptureInput<T> setCaptureVideoSize(final String size) {
        return addArguments("-video_size", size);
    }

    /**
     * Sets the video region offsets.
     * <p>
     * Note: this option doesn't work for some capture devices.
     *
     * @param xOffset x offset
     * @param yOffset y offset
     * @return this
     */
    public abstract CaptureInput<T> setCaptureVideoOffset(int xOffset, int yOffset);

    /**
     * Instructs ffmpeg to capture mouse cursor.
     * <p>
     * <b>Note</b>: this feature is not supported on all devices.
     *
     * @param captureCursor true to capture cursor
     * @return this
     */
    public abstract CaptureInput<T> setCaptureCursor(boolean captureCursor);

    /**
     * Creates {@link CaptureInput} with automatically selected capture device.
     * <p>
     * Note: consider using concrete subclass factory methods.
     *
     * @return CaptureInput
     * @see LinuxX11Grab
     * @see MacOsAvFoundation
     * @see WindowsGdiGrab
     * @see WindowsDirectShow
     */
    public static CaptureInput<?> captureDesktop() {
        CaptureInput<?> result = null;
        if (OS.IS_LINUX) {
            result = LinuxX11Grab.captureDesktop();
        } else if (OS.IS_MAC) {
            result = MacOsAvFoundation.captureDesktop();
        } else if (OS.IS_WINDOWS) {
            result = WindowsGdiGrab.captureDesktop();
        }

        if (result == null) {
            throw new JaffreeException("Could not detect OS");
        }

        return result;
    }

    /**
     * {@link Input} implementation for Direct Show capture device.
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-devices.html#dshow">dshow documentation</a>
     * @see <a href="https://trac.ffmpeg.org/wiki/DirectShow">dshow ffmepg wiki</a>
     */
    public static class WindowsDirectShow extends CaptureInput<WindowsDirectShow> {
        private static final Logger LOGGER = LoggerFactory.getLogger(WindowsDirectShow.class);

        /**
         * @param input input identifier
         */
        public WindowsDirectShow(final String input) {
            super(input);
        }

        /**
         * Creates {@link WindowsDirectShow} capture device.
         *
         * @param videoDevice video device identifier
         * @return WindowsDirectShow
         */
        public static WindowsDirectShow captureVideo(final String videoDevice) {
            return captureVideoAndAudio(videoDevice, null);
        }

        /**
         * Creates {@link WindowsDirectShow} capture device.
         *
         * @param audioDevice audio device identifier
         * @return WindowsDirectShow
         */
        public static WindowsDirectShow captureAudio(final String audioDevice) {
            return captureVideoAndAudio(null, audioDevice);
        }

        /**
         * Creates {@link WindowsDirectShow} capture device.
         *
         * @param videoDevice video device identifier
         * @param audioDevice audio device identifier
         * @return WindowsDirectShow
         */
        public static WindowsDirectShow captureVideoAndAudio(final String videoDevice,
                                                             final String audioDevice) {
            String input = "";
            if (videoDevice != null) {
                input = "video=" + videoDevice;
            }
            if (audioDevice != null) {
                if (!input.isEmpty()) {
                    input += ":";
                }
                input += "audio=" + audioDevice;
            }

            return new WindowsDirectShow(input)
                    .setFormat("dshow");
        }

        /**
         * <b>Not supported for {@link WindowsDirectShow}</b>.
         *
         * @param captureCursor true to capture cursor
         * @return this
         */
        @Override
        public WindowsDirectShow setCaptureCursor(final boolean captureCursor) {
            LOGGER.warn("Cursor capture option is not supported");
            return this;
        }

        /**
         * <b>Not supported for {@link WindowsDirectShow}</b>.
         * <p>
         * {@inheritDoc}
         *
         * @param xOffset x offset
         * @param yOffset y offset
         * @return this
         */
        @Override
        public WindowsDirectShow setCaptureVideoOffset(final int xOffset, final int yOffset) {
            LOGGER.warn("Capture offset option is not supported");
            return this;
        }
    }

    /**
     * {@link Input} implementation for GDI Grab capture device.
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-devices.html#gdigrab">gdigrab documentation</a>
     */
    public static class WindowsGdiGrab extends CaptureInput<WindowsGdiGrab> {

        /**
         * @param input input identifier
         */
        public WindowsGdiGrab(final String input) {
            super(input);
        }

        /**
         * Creates {@link WindowsGdiGrab} capture device to capture desktop.
         *
         * @return WindowsGdiGrab
         */
        public static WindowsGdiGrab captureDesktop() {
            return new WindowsGdiGrab("desktop")
                    .setFormat("gdigrab");
        }

        /**
         * Creates {@link WindowsGdiGrab} capture device.
         * Cpatures single window with specified title.
         *
         * @param windowTitle window title
         * @return WindowsGdiGrab
         */
        public static WindowsGdiGrab captureWindow(final String windowTitle) {
            return new WindowsGdiGrab("title=" + windowTitle)
                    .setFormat("gdigrab");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WindowsGdiGrab setCaptureCursor(final boolean captureCursor) {
            String argValue = captureCursor ? "1" : "0";
            return addArguments("-draw_mouse", argValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WindowsGdiGrab setCaptureVideoOffset(final int xOffset, final int yOffset) {
            return addArguments("-offset_x", String.valueOf(xOffset))
                    .addArguments("-offset_y", String.valueOf(yOffset));
        }
    }

    /**
     * {@link Input} implementation for AVFoundation capture device.
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-devices.html#avfoundation">avfoundation
     * documentation</a>
     */
    public static class MacOsAvFoundation extends CaptureInput<MacOsAvFoundation> {
        private static final Logger LOGGER = LoggerFactory.getLogger(MacOsAvFoundation.class);

        /**
         * @param input input identifier
         */
        public MacOsAvFoundation(final String input) {
            super(input);
        }

        /**
         * Creates {@link MacOsAvFoundation} capture device to capture desktop.
         *
         * @return MacOsAvFoundation
         */
        public static MacOsAvFoundation captureDesktop() {
            return captureVideoAndAudio("default", null);
        }

        /**
         * Creates {@link MacOsAvFoundation} capture device.
         *
         * @param videoDevice video deice or null
         * @param audioDevice audio device or null
         * @return MacOsAvFoundation
         */
        public static MacOsAvFoundation captureVideoAndAudio(final String videoDevice,
                                                             final String audioDevice) {
            String videoDeviceOrNone = videoDevice == null ? "none" : videoDevice;
            String audioDeviceOrNone = audioDevice == null ? "none" : audioDevice;

            return new MacOsAvFoundation(videoDeviceOrNone + ":" + audioDeviceOrNone)
                    .setFormat("avfoundation");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MacOsAvFoundation setCaptureCursor(final boolean captureCursor) {
            String argValue = captureCursor ? "1" : "0";
            return addArguments("-capture_cursor", argValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MacOsAvFoundation setCaptureVideoOffset(final int xOffset, final int yOffset) {
            LOGGER.warn("Capture offset option is not supported");
            return this;
        }
    }

    /**
     * @see <a href="https://ffmpeg.org/ffmpeg-devices.html#x11grab">x11grab documentation</a>
     */
    public static class LinuxX11Grab extends CaptureInput<LinuxX11Grab> {

        /**
         * @param input input identifier
         */
        public LinuxX11Grab(final String input) {
            super(input);
        }

        /**
         * Creates {@link LinuxX11Grab} capture device to capture desktop.
         *
         * @return LinuxX11Grab
         */
        public static LinuxX11Grab captureDesktop() {
            return captureDisplayAndScreen(0, 0);
        }

        /**
         * Creates {@link LinuxX11Grab} capture device.
         *
         * @param display display
         * @param screen  screen
         * @return LinuxX11Grab
         */
        public static LinuxX11Grab captureDisplayAndScreen(final int display, final int screen) {
            return captureHostDisplayAndScreen("", display, screen);
        }

        /**
         * Creates {@link LinuxX11Grab} capture device.
         *
         * @param host    host
         * @param display display
         * @param screen  screen
         * @return LinuxX11Grab
         */
        public static LinuxX11Grab captureHostDisplayAndScreen(final String host, final int display,
                                                               final int screen) {
            return new LinuxX11Grab(host + ":" + display + "." + screen)
                    .setFormat("x11grab");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LinuxX11Grab setCaptureCursor(final boolean captureCursor) {
            String argValue = captureCursor ? "1" : "0";
            return addArguments("-draw_mouse", argValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LinuxX11Grab setCaptureVideoOffset(final int xOffset, final int yOffset) {
            return addArguments("-grab_x", String.valueOf(xOffset))
                    .addArguments("-grab_y", String.valueOf(yOffset));
        }
    }
}
