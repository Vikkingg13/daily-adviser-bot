package com.github.vikkingg13.service.impl;

import com.github.vikkingg13.config.BotConfig;
import com.github.vikkingg13.keyboard.Keyboards;
import com.github.vikkingg13.model.db.Person;
import com.github.vikkingg13.service.BotService;
import com.github.vikkingg13.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@Service
public class BotServiceImpl extends BotService {

    @Autowired TimeServiceImpl timeService;

    @Autowired
    private PersonService personService;

    @Autowired
    private final BotConfig botConfig;

    private Set<Long> ignoredSetId = new HashSet<>();

    public BotServiceImpl(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void clearIgnoredSet() {
        ignoredSetId.clear();
    }

    public void sendMessageAll(String textToMessage) {
        List<Person> personList = personService.findAll();
        for (Person person: personList) {
            if (ignoredSetId.contains(person.getId())) {
                continue;
            }
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(person.getId())
                    .text(textToMessage)
                    .replyMarkup(Keyboards.CONFIRMING_KEYBOARD)
                    .build();

            try {
                execute(sendMessage);
                ignoredSetId.add(person.getId());
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            callbackQueryProcess(update);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageProcess(update);
        }
    }

    private void messageProcess(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getChat().getFirstName();
        switch (messageText) {
            case "/start" -> {
                String answer = startCommandReceived(name);
                personService.save(new Person(chatId, name));
                sendMessage(chatId, answer);
            }
            case "/schedule" -> {
                String listTime = getTimeList();
                sendMessage(chatId, listTime);
            }
            case "/help" -> {
                String text = """
                        Всё просто. Я буду каждый день по графику присылать ценные советы.
                        От тебя требуется лишь нажимать кнопку "ХОРОШО" под советом,
                        тогда я буду знать что тебе интересно и продолжу делиться жизненной мудростью.
                        """;
                sendMessage(chatId, text);
            }
            default -> sendMessage(chatId,
                    "Извини, я не понимаю.");
        }
    }

    private String getTimeList() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add("Расписание: ");
        for (TimeServiceImpl.Time time : timeService.getTimeSet()) {
            joiner.add(time.toString());
        }
        return joiner.toString();
    }

    private void callbackQueryProcess(Update update) {
        var callbackQuery = update.getCallbackQuery();
        if ("confirm".equals(callbackQuery.getData())) {
            var editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(null)
                    .build();
            ignoredSetId.remove(callbackQuery.getMessage().getChatId());
            try {
                execute(editMessageReplyMarkup);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String startCommandReceived(String contactName) {
        return String.format("Хэй! Какая встреча, %s. Рад тебя видеть.", contactName);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(textToSend)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
