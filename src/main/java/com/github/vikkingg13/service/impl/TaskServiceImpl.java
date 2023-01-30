package com.github.vikkingg13.service.impl;

import com.github.vikkingg13.model.Message;
import com.github.vikkingg13.service.BotService;
import com.github.vikkingg13.service.TaskService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static com.github.vikkingg13.Util.StringSpliterator.splitToPairToken;
import static com.github.vikkingg13.model.Message.textToMessage;

@Log4j2
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private BotService botService;

    @Autowired
    private TimeServiceImpl timeService;

    public Map.Entry<File, List<CronTask>> parseContent(Map.Entry<File, String> entry) {
        String[] tokens = splitToPairToken(entry.getValue());
        List<TimeServiceImpl.Time> times = timeService.textToTimeList(tokens[0]);
        LinkedList<Message> messages = new LinkedList<>(textToMessage(tokens[1]));
        Runnable runnable = createRunnable(messages);
        List<CronTask> taskList = createTasks(times, runnable);
        return Map.entry(entry.getKey(), taskList);
    }

    private Runnable createRunnable(LinkedList<Message> messages) {
        Collections.shuffle(messages);
        return () -> {
            Message message = messages.poll();
            messages.offer(message);
            botService.sendMessageAll(message.getText());
        };
    }

    private List<CronTask> createTasks(List<TimeServiceImpl.Time> times, Runnable runnable) {
        List<CronTask> list = new ArrayList<>();
        for (TimeServiceImpl.Time time : times) {
            CronTask task = new CronTask(runnable, time.toCronTrigger());
            log.log(Level.INFO, "Task expression: {}", task.getExpression());
            list.add(task);
        }
        return list;
    }
}