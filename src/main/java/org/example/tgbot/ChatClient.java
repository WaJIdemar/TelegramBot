package org.example.tgbot;

import org.example.tgbot.keyboards.CallbackKeyboard;
import org.example.tgbot.keyboards.Keyboard;
import org.example.tgbot.keyboards.UrlKeyboard;

import java.util.List;

public interface ChatClient {

    void sendMessage(Long chatId, String text, Keyboard keyboard);
    void sendMessage(Long chatId, String text, CallbackKeyboard keyboard);
    void sendMessage(Long chatId, String text);
    void editMessage(Long chatId, Integer messageId, String text);
    void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard);
    void sendMessage(Long chatId, String text, String photoUrl, UrlKeyboard keyboard);
    void sendMessage(Long chatId, String text, UrlKeyboard keyboard);
}
