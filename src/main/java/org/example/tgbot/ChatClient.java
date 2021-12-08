package org.example.tgbot;

public interface ChatClient {

    void sendMessage(Long chatId, String text, Keyboard keyboard);
    void sendMessage(Long chatId, String text, InlineKeyboard keyboard);
    void editMessage(Long chatId, Integer messageId, String text);
}
