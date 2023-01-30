package com.github.vikkingg13.Util;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentValidator {

    private static final Pattern pattern = Pattern.compile(
            "((^|\s|\n|\r\n)\\d{2}:\\d{2})+(\n|\r\n){2}((\n|\r\n){0,2}\\p{all}+)+",
            Pattern.UNICODE_CHARACTER_CLASS);
    public static boolean validate(Map.Entry<File, String> entry) {
        Matcher mather = pattern.matcher(entry.getValue());
        return entry.getKey().getName().endsWith(".txt") && mather.matches();
    }
}
