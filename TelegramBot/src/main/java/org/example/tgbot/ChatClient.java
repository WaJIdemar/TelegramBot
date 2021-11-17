package org.example.tgbot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ChatClient {
    void sendMessage(Long chatId, String text, Keyboard keyboard);

    void setButtons(SendMessage sendMessage, Keyboard keyboard);
}
