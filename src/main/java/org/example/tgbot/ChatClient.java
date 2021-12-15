package org.example.tgbot;

public interface ChatClient {

    void sendMessage(Long chatId, String text, Keyboard keyboard);
    void sendMessage(Long chatId, String text, CallbackKeyboard keyboard);
    void sendMessage(Long chatId, String text);
    void editMessage(Long chatId, Integer messageId, String text);
    void sendMessage(Long chatId, String text, UrlKeyboard keyboard);
}
