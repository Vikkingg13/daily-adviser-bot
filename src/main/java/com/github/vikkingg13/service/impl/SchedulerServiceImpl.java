package com.github.vikkingg13.service.impl;

import com.github.vikkingg13.service.SchedulerService;
import com.github.vikkingg13.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private TaskService taskService;

    private Map<File, List<ScheduledFuture<?>>> futureMap = new HashMap<>();

    @Override
    public void addTask(Map.Entry<File, List<CronTask>> entry) {
        List<ScheduledFuture<?>> futureList = new ArrayList<>();
        for (CronTask task : entry.getValue()) {
            ScheduledFuture<?> future = scheduler.schedule(task.getRunnable(), task.getTrigger());
            futureList.add(future);
        }
        List<ScheduledFuture<?>> prevFutureList = futureMap.put(entry.getKey(), futureList);
        if (prevFutureList != null) {
            removeTask(prevFutureList);
        }
    }

    @Override
    public void removeTask(File file) {
        var optional = Optional.ofNullable(futureMap.remove(file));
        optional.ifPresent(
                futureList -> futureList.forEach(
                        future -> future.cancel(true)
                )
        );
    }

    private void removeTask(List<ScheduledFuture<?>> futureList) {
        for (ScheduledFuture<?> future : futureList) {
            future.cancel(true);
        }

    }
}
