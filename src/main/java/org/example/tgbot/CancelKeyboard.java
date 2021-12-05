package org.example.tgbot;

import java.util.List;

public class CancelKeyboard extends Keyboard{
    public CancelKeyboard() {
        super(List.of(List.of("Отменить")));
    }
}
