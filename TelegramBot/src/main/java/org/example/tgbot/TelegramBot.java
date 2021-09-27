package org.example.tgbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String Token = "2043487271:AAGgorrTG6RUcX9Ssmly3OtBuNmRYgEHby0";
    private static final String Name = "Topkub";

    @Override
    public String getBotUsername() {
        return Name;
    }

    @Override
    public String getBotToken() {
        return Token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(UserData.massageToUser(update.getMessage().getChatId(), update.getMessage().getText()));

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
