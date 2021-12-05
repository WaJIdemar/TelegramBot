package org.example.tgbot;

import javafx.util.Pair;

import java.util.List;

public class InlineKeyboard {
    public List<List<Pair<String, String>>> buttons;

    public InlineKeyboard(List<List<Pair<String, String>>> buttons) {
        this.buttons = buttons;
    }
}
