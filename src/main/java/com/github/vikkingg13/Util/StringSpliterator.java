package com.github.vikkingg13.Util;

public class StringSpliterator {

    public static final String SEPARATOR = "(\n|\r\n){2}";

    public static String[] splitToPairToken(String string) {
        String[] tokens = string.split(SEPARATOR, 2);
        if (tokens.length == 2) {
            return tokens;
        } else {
            throw new RuntimeException("File parsing failed");
        }
    }

    public static String[] splitToArrayTokens(String string) {
       return string.split(SEPARATOR);
    }
}
