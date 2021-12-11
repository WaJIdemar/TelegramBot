package org.example.tgbot;

import java.util.List;

public class InlineKeyboard {
    public List<List<InlineButton>> buttons;

    public InlineKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
