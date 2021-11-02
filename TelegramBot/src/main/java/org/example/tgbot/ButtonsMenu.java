package org.example.tgbot;

import java.util.ArrayList;
import java.util.List;

public class ButtonsMenu {
    private final List<List<String>> defaultButtonsMenu;

    public ButtonsMenu(){
        defaultButtonsMenu = new ArrayList<>();
        defaultButtonsMenu.add(List.of("Привет", "Дай определение"));
        defaultButtonsMenu.add(List.of("Помощь"));
    }

    public List<List<String>> getDefaultButtonsMenu(){
        return defaultButtonsMenu;
    }
}
