package com.github.vikkingg13.model;

import com.github.vikkingg13.Util.StringSpliterator;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Message {

    public static final String SEPARATOR = "(\n|\r\n){2}";

    private String text;

    public Message(String text) {
        this.text = text;
    }

    public static List<Message> textToMessage(String text) {
        String[] array = StringSpliterator.splitToArrayTokens(text);
        return Arrays.stream(array).map(Message::new).collect(Collectors.toCollection(LinkedList::new));
    }
}
