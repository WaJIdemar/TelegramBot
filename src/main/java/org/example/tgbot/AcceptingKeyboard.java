package org.example.tgbot;


import javafx.util.Pair;

import java.util.List;

public class AcceptingKeyboard extends InlineKeyboard {
    public AcceptingKeyboard(Integer index) {
        super(List.of(List.of(new InlineButton("Принять", "accept_" + index),
                new InlineButton("Отклонить", "reject_" + index),
                new InlineButton("Бан", "ban_" + index))));
    }
}
