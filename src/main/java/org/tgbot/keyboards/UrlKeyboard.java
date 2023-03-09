package org.tgbot.keyboards;

import org.tgbot.buttons.InlineButton;

import java.util.List;

public class UrlKeyboard {
    public final List<List<InlineButton>> buttons;

    public UrlKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
