package org.example.tgbot.keyboards;

import org.example.tgbot.buttons.InlineButton;

import java.util.List;

public class UrlKeyboard {
    public List<List<InlineButton>> buttons;

    public UrlKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
