package org.example.tgbot;

import java.util.List;

public class UrlKeyboard {
    public List<List<InlineButton>> buttons;

    public UrlKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
