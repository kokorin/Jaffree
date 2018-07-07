package com.github.kokorin.jaffree;

public class OS {
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    /*public static Path getExecutablePath(String executable, Path baseDir) {
        if (IS_WINDOWS) {
            executable += ".exe";
        }
    }*/

}
