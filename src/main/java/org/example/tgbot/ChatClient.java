package org.example.tgbot;

import java.util.List;

public interface ChatClient {

    void sendMessage(Long chatId, String text, Keyboard keyboard);
    void sendMessage(Long chatId, String text, CallbackKeyboard keyboard);
    void sendMessage(Long chatId, String text);
    void editMessage(Long chatId, Integer messageId, String text);
    void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard);
}
