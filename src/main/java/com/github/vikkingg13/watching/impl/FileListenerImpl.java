package com.github.vikkingg13.watching.impl;

import com.github.vikkingg13.Util.ContentValidator;
import com.github.vikkingg13.service.ScannerService;
import com.github.vikkingg13.service.SchedulerService;
import com.github.vikkingg13.service.TaskService;
import com.github.vikkingg13.service.impl.TimeServiceImpl;
import com.github.vikkingg13.watching.FileEvent;
import com.github.vikkingg13.watching.FileListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.github.vikkingg13.Util.StringSpliterator.splitToPairToken;

@Component
@Log4j2
public class FileListenerImpl implements FileListener {

    @Autowired
    private TimeServiceImpl timeService;

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void onCreated(FileEvent event) {
        log.info("File created");
        fileTrack(event);
    }

    @Override
    public void onModified(FileEvent event) {
        log.info("File modified");
        fileTrack(event);
    }

    @Override
    public void onDeleted(FileEvent event) {
        log.info("File deleted");
        schedulerService.removeTask(event.getFile());
        timeService.removeFromMap(event.getFile());

    }

    private void fileTrack(FileEvent event) {
        log.info("Found change in file: {}", event.getFile().getName());
        if (event.getFile().exists() && event.getFile().getName().endsWith(".txt")) {
            Map.Entry<File, String> entry = scannerService.scanFile(event.getFile());
            if (ContentValidator.validate(entry)) {
                log.info("File with name: {} has validated", entry.getKey().getName());
                Map.Entry<File, List<CronTask>> taskEntry = taskService.parseContent(entry);
                addToTimeList(entry);
                schedulerService.addTask(taskEntry);
            }
        }
    }

    private void addToTimeList(Map.Entry<File, String> entry) {
        String[] tokens = splitToPairToken(entry.getValue());
        List<TimeServiceImpl.Time> list = timeService.textToTimeList(tokens[0]);
        timeService.insertIntoMap(entry.getKey(), list);
    }
}