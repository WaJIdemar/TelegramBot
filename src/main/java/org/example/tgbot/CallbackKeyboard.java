package org.example.tgbot;

import java.util.List;

public class CallbackKeyboard {
    public List<List<InlineButton>> buttons;

    public CallbackKeyboard(List<List<InlineButton>> buttons) {
        this.buttons = buttons;
    }
}
