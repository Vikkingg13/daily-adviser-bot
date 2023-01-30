package com.github.vikkingg13.config;

import com.github.vikkingg13.Util.ContentValidator;
import com.github.vikkingg13.service.BotService;
import com.github.vikkingg13.service.ScannerService;
import com.github.vikkingg13.service.TaskService;
import com.github.vikkingg13.service.impl.SchedulerServiceImpl;
import com.github.vikkingg13.service.impl.TimeServiceImpl;
import com.github.vikkingg13.watching.FileListener;
import com.github.vikkingg13.watching.FileWatcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class BotInitializer {

    @Autowired
    private BotService botService;

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SchedulerServiceImpl schedulerService;

    @Autowired
    private TimeServiceImpl timeService;

    @Autowired
    private FileListener fileListener;

    @Value("${bot.task.home}")
    private String path;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(botService);

        contentLoad();

        FileWatcher watcher = new FileWatcher(new File(path));
        watcher.addListener(fileListener);
        watcher.watch();
    }

    private void contentLoad() {
        Map<File, String> contentMap = scannerService.scanFolder(new File(path));
        Map<File, String> validateContentMap = contentMap.entrySet().stream()
                .filter(ContentValidator::validate)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<File, List<CronTask>> taskMap = validateContentMap.entrySet().stream()
                .filter(ContentValidator::validate)
                .peek(entry -> log.info("File with name: {} has validated", entry.getKey().getName()))
                .map(taskService::parseContent)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        taskMap.entrySet().forEach(schedulerService::addTask);
        validateContentMap.forEach(
                (key, value) -> timeService.insertIntoMap(key, timeService.textToTimeList(value)));
    }
}
