package org.example.tgbot;

import java.util.List;

public class RandomOrCertainTermKeyBoard extends Keyboard{
    public RandomOrCertainTermKeyBoard() {
        super(List.of(List.of("Рандомное определение", "Конкретное определение"), List.of("/Отмена")));
    }
}
