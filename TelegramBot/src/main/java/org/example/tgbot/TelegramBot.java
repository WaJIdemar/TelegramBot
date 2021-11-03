package org.example.tgbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final String Name;
    private final String Token;
    private final BotLogic botLogic;
    private final AppVk appVk;
    private Boolean appVkStatus;

    public TelegramBot(String name, String token, BotLogic botLogic) {
        Name = name;
        Token = token;
        this.botLogic = botLogic;
        this.appVk = new AppVk();
        appVkStatus = true;
    }

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
            try {
                ResponseToUser responseToUser = botLogic.responseUser(update.getMessage().getChatId(),
                        update.getMessage().getText());
                message.setText(responseToUser.getMessageToUser());
                setButtons(message, responseToUser.getButtonsMenuStatus());
                execute(message); // Call method to send the message
            } catch (Exception e) {
                message.setChatId(System.getenv("MY_CHAT_ID_TELEGRAM"));
                message.setText("Ошибка telegram-bot'a:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public synchronized void setButtons(SendMessage sendMessage, List<List<String>> buttonsMenu) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (List<String> row : buttonsMenu) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : row)
                keyboardRow.add(new KeyboardButton(button));
            keyboard.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
