package com.github.vikkingg13.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface Keyboards {

    InlineKeyboardMarkup CONFIRMING_KEYBOARD = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(
                    InlineKeyboardButton.builder()
                    .text("Хорошо")
                    .callbackData("confirm")
                    .build()))
            .build();


}
