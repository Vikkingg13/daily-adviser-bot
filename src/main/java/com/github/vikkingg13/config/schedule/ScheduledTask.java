package com.github.vikkingg13.config.schedule;

import com.github.vikkingg13.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private BotService botService;

    @Scheduled(cron = "${scheduled.reset}")
    public void resetIgnoredSet() {
        botService.clearIgnoredSet();
    }
}
