package com.github.vikkingg13.service;

import org.springframework.scheduling.config.CronTask;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface SchedulerService {

    void addTask(Map.Entry<File, List<CronTask>> map);

    void removeTask(File file);
}
