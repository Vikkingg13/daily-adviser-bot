package com.github.vikkingg13.service;

import org.springframework.scheduling.config.CronTask;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TaskService {
    Map.Entry<File, List<CronTask>> parseContent(Map.Entry<File, String> entry);
}
