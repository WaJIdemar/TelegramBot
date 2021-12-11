package org.example.tgbot;

import java.util.List;

public class UrlKeyboard extends InlineKeyboard{
    public UrlKeyboard(String url) {
        super(List.of(List.of(new InlineButton("Открыть в ВК", url))));
    }
}
