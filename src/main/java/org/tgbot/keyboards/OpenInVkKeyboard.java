package org.tgbot.keyboards;

import org.tgbot.buttons.InlineButton;

import java.util.List;

public class OpenInVkKeyboard extends UrlKeyboard {
    public OpenInVkKeyboard(String url) {
        super(List.of(List.of(new InlineButton("Открыть в Вк", url))));
    }
}
