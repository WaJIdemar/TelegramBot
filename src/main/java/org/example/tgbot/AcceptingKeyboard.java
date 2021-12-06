package org.example.tgbot;


import javafx.util.Pair;

import java.util.List;

public class AcceptingKeyboard extends InlineKeyboard {
    public AcceptingKeyboard(Integer index, Long id) {
        super(List.of(List.of(new Pair<>("Принять", "принять_" + index),
                new Pair<>("Отклонить", "отклонить_" + index),
                new Pair<>("Бан", "бан_" + id))));
    }
}
