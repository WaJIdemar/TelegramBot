package org.example.tgbot.keyboards;


import org.example.tgbot.buttons.InlineButton;

import java.util.List;

public class AcceptingKeyboard extends CallbackKeyboard {
    public AcceptingKeyboard(Long index) {
        super(List.of(List.of(new InlineButton("Принять", "accept_" + index),
                new InlineButton("Отклонить", "reject_" + index),
                new InlineButton("Бан", "ban_" + index))));
    }
}
