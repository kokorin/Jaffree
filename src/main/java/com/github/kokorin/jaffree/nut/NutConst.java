package com.github.kokorin.jaffree.nut;

public class NutConst {
    public static String FILE_ID = "nut/multimedia container";
    public static byte[] FILE_ID_BYTES = toCStr(FILE_ID);


    private NutConst() {
    }

    private static byte[] toCStr(String value) {
        byte[] result = new byte[value.length() + 1];
        for (int i = 0; i < value.length(); i++) {
            result[i] = (byte) value.charAt(i);
        }
        return result;
    }


}
