package org.tgbot.keyboards;

import java.util.List;

public class YesOrNoKeyboard extends Keyboard {
    public YesOrNoKeyboard() {
        super(List.of(List.of("Да", "Нет")));
    }
}
