package com.github.vikkingg13.service.impl;

import com.github.vikkingg13.service.ScannerService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class ScannerServiceImpl implements ScannerService {

    private static final String SEPARATOR = System.lineSeparator();

    public Map<File, String> scanFolder(File dir) {
        var files = getFiles(dir);
        Map<File, String> map = new HashMap<>();
        for (File file : files) {
            String text = readFile(file);
            map.put(file, text);
        }
        return map;
    }

    public Map.Entry<File, String> scanFile(File file) {
        String content = readFile(file);
        return Map.entry(file, content);
    }

    private File[] getFiles(File dir) {
        File[] files;
        FileFilter filter = file -> file.getName().endsWith(".txt");
        if (dir.exists() && dir.isDirectory()) {
            files = dir.listFiles(filter);
        } else {
            throw new RuntimeException("File isn't exists or isn't directory");
        }
        return files;
    }

    public static String readFile(File file) {
        StringJoiner joiner = new StringJoiner(SEPARATOR);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                joiner.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("File not found");
        }
        return joiner.toString();
    }
}
