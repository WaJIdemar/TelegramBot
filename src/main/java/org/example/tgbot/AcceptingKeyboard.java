package org.example.tgbot;


import javafx.util.Pair;

import java.util.List;

public class AcceptingKeyboard extends InlineKeyboard {
    public AcceptingKeyboard(Integer index, Long id) {
        super(List.of(List.of(new Pair<>("Принять", "accept_" + index),
                new Pair<>("Отклонить", "reject_" + index),
                new Pair<>("Бан", "ban_" + id))));
    }
}
