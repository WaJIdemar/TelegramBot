package org.example.tgbot;


import java.util.List;

public class AcceptingKeyboard extends CallbackKeyboard {
    public AcceptingKeyboard(Long index) {
        super(List.of(List.of(new InlineButton("Принять", "accept_" + index),
                new InlineButton("Отклонить", "reject_" + index),
                new InlineButton("Бан", "ban_" + index))));
    }
}
