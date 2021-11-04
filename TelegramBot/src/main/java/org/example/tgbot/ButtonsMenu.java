package org.example.tgbot;

import java.util.ArrayList;
import java.util.List;

public class ButtonsMenu {
    private final List<List<String>> defaultButtonsMenu;
    private final List<List<String>> randomOrCertainTerm;
    private final List<List<String>> yesOrNo;
    private final List<List<String>> cancel;

    public ButtonsMenu() {
        defaultButtonsMenu = new ArrayList<>();
        defaultButtonsMenu.add(List.of("Привет", "Дай определение"));
        defaultButtonsMenu.add(List.of("Помощь"));
        randomOrCertainTerm = new ArrayList<>();
        randomOrCertainTerm.add(List.of("Рандомное определение", "Конкретное определение"));
        randomOrCertainTerm.add(List.of(("/Отмена")));
        yesOrNo = new ArrayList<>();
        yesOrNo.add(List.of("Да", "Нет"));
        cancel = new ArrayList<>();
        cancel.add(List.of("/Отмена"));
    }

    public List<List<String>> getDefaultButtonsMenu() {
        return defaultButtonsMenu;
    }

    public List<List<String>> getRandomOrCertainTerm() {
        return randomOrCertainTerm;
    }

    public List<List<String>> getYesOrNo() {
        return yesOrNo;
    }

    public List<List<String>> getCancel() {
        return cancel;
    }
}
