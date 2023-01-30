package com.github.vikkingg13.service.impl;

import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TimeServiceImpl {
    @Value("${scheduled.daysOfWeek}")
    private String daysOfWeek;

    private  final String REGEX_TIME_TO_TRIGGER = "\\d{2}:\\d{2}";

    private  Map<File, List<Time>> timeMap = new HashMap<>();

    public  void insertIntoMap(File file, List<Time> list) {
        timeMap.put(file, list);
    }

    public  Time textToTime(String text) {
        if (text.matches(REGEX_TIME_TO_TRIGGER)) {
            String[] parts = text.split(":");
            return new Time(parts[0], parts[1]);
        } else {
            throw new RuntimeException("Illegal time format");
        }
    }

    public void removeFromMap(File file) {
        timeMap.remove(file);
    }

    public Set<Time> getTimeSet() {
        return timeMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public  List<Time> textToTimeList(String text) {
        Pattern pattern = Pattern.compile(REGEX_TIME_TO_TRIGGER);
        Matcher matcher = pattern.matcher(text);
        List<Time> times = new ArrayList<>();
        while (matcher.find()) {
            times.add(textToTime(matcher.group()));
        }
        return times;
    }

    @EqualsAndHashCode
    public class Time implements Comparable<Time> {

        private final String hour;
        private final String minute;
        Time(String hour, String minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public CronTrigger toCronTrigger() {
            var cron = String.format("0 %s %s * * %s", minute, hour, daysOfWeek);
            if (CronExpression.isValidExpression(cron)) {
                return new CronTrigger(cron);
            } else {
                throw new IllegalArgumentException("Invalid Cron Expression");
            }
        }

        @Override
        public String toString() {
            return String.format("%s:%s", hour, minute);
        }

        @Override
        public int compareTo(Time o) {
            int result = hour.compareTo(o.hour);
            return result == 0 ? minute.compareTo(o.minute) : result;
        }
    }

}
