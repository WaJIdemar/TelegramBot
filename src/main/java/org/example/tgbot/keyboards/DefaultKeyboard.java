package org.example.tgbot.keyboards;

import java.util.List;

public class DefaultKeyboard extends Keyboard {
    public DefaultKeyboard() {
        super(List.of(List.of("Привет", "Дай определение"),List.of("Помощь")));
    }
}
