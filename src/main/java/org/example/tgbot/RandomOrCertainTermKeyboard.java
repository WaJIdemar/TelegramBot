package org.example.tgbot;

import java.util.List;

public class RandomOrCertainTermKeyboard extends Keyboard{
    public RandomOrCertainTermKeyboard() {
        super(List.of(List.of("Рандомное определение", "Конкретное определение"), List.of("Назад")));
    }
}
