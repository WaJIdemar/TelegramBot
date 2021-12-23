package org.example.tgbot;

import java.util.List;

public class TestChatClient implements ChatClient {

    public String prevMes = "";
    public String currentMes;
    public Long currentChatId;

    @Override
    public void sendMessage(Long chatId, String text, Keyboard keyboard) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, CallbackKeyboard keyboard) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void editMessage(Long chatId, Integer messageId, String text) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, String photoUrl, UrlKeyboard keyboard) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, UrlKeyboard keyboard) {
        prevMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }
}
