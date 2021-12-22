package org.example.tgbot;

import java.util.List;

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
    public void sendMessage(Long chatId, String text, CallbackKeyboard keyboard) {
        telegramBot.sendMessage(chatId, text, keyboard);
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        telegramBot.sendMessage(chatId, text);
    }

    @Override
    public void editMessage(Long chatId, Integer messageId, String text) {
        telegramBot.editMessage(chatId, messageId, text);
    }

    @Override
    public void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard) {
        telegramBot.sendMessage(chatId, text, photosUrl, keyboard);
    }

}
