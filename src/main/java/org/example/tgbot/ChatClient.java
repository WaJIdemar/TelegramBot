package org.example.tgbot;

import java.util.List;

public interface ChatClient {

    void sendMessage(Long chatId, String text, Keyboard keyboard);
    void sendMessage(Long chatId, String text, InlineKeyboard keyboard);
    void editMessage(Long chatId, Integer messageId, String text);
    void sendPostToChannel(String text, List<String> photosUrl, String postUrl);
}
