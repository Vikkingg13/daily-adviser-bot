package com.github.vikkingg13.service;

import java.io.File;
import java.util.Map;

public interface ScannerService {

    Map<File, String> scanFolder(File file);
    Map.Entry<File, String> scanFile(File file);
}
