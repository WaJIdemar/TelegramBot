package org.tgbot.keyboards;

import org.tgbot.buttons.InlineButton;

import java.util.List;

public class CallbackKeyboard {
    public final List<List<InlineButton>> buttons;

    public CallbackKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
