package org.tgbot.tests.model;

import org.tgbot.ChatClient;
import org.tgbot.keyboards.CallbackKeyboard;
import org.tgbot.keyboards.Keyboard;
import org.tgbot.keyboards.UrlKeyboard;

import java.util.List;

public class TestChatClient implements ChatClient {

    public String currentMes;
    public Long currentChatId;

    public String previousMes;

    public Keyboard currentKeyboard;

    public String currentMesCallback;

    public CallbackKeyboard currentCallbackKeyboard;

    public Long currentChatIdCallback;

    @Override
    public void sendMessage(Long chatId, String text, Keyboard keyboard) {

        previousMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
        currentKeyboard = keyboard;
    }

    @Override
    public void sendMessage(Long chatId, String text, CallbackKeyboard keyboard) {

        currentMesCallback = text;
        currentChatIdCallback = chatId;
        currentCallbackKeyboard = keyboard;
    }

    @Override
    public void sendMessage(Long chatId, String text) {

        previousMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void editMessage(Long chatId, Integer messageId, String text) {

        previousMes = currentMes;
        currentMes = text;
        currentChatIdCallback = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard) {

        previousMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, String photoUrl, UrlKeyboard keyboard) {

        previousMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    @Override
    public void sendMessage(Long chatId, String text, UrlKeyboard keyboard) {

        previousMes = currentMes;
        currentMes = text;
        currentChatId = chatId;
    }

    public void clear() {

        previousMes = null;
        currentMes = null;
        currentChatId = null;
        currentKeyboard = null;
    }
}
