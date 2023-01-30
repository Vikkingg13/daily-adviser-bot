package com.github.vikkingg13.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public abstract class BotService extends TelegramLongPollingBot  {

    abstract public void clearIgnoredSet();
    abstract public void sendMessageAll(String textToSend);
}
