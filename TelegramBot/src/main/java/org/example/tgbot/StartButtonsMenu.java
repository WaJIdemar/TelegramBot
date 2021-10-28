package org.example.tgbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartButtonsMenu implements ButtonsMenu {
    private final List<List<String>> rows;

    public StartButtonsMenu() {
        rows = new ArrayList<>();
        rows.add(Arrays.asList("Привет", "Дай определение"));
        rows.add(List.of("Помощь"));
    }

    @Override
    public Integer getCountRows() {
        return 2;
    }

    @Override
    public List<String> getButtonsRow(int index) {
        return rows.get(index);
    }
}
