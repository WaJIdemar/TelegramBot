package org.example.tgbot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramChatClient implements ChatClient {

    private final TelegramBot telegramBot;

    public TelegramChatClient(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void sendMessage(Long chatId, String text, Keyboard keyboard) {
        telegramBot.sendMessage(chatId, text, keyboard);
    }

    @Override
    public void setButtons(SendMessage sendMessage, Keyboard keyboard) {
        telegramBot.setButtons(sendMessage, keyboard);
    }
}
