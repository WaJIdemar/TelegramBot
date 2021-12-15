package org.example.tgbot;

import java.util.List;

public class OpenInVkKeyboard extends UrlKeyboard{
    public OpenInVkKeyboard(String url) {
        super(List.of(List.of(new InlineButton("Открыть в Вк", url))));
    }
}
