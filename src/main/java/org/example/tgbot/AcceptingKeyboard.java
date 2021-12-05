package org.example.tgbot;


import javafx.util.Pair;

import java.util.List;

public class AcceptingKeyboard extends InlineKeyboard {
    public AcceptingKeyboard() {
        super(List.of(List.of(new Pair<>("Принять", "accept"), new Pair<>("Отклонить", "decline"),
                new Pair<>("Бан", "ban"))));
    }
}
