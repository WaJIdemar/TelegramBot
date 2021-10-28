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
    private final String Token = System.getenv("TELEGRAM_BOT_TOKEN");
    private final String Name = System.getenv("TELEGRAM_BOT_NAME");
    private final UserData userData = new UserData();
    private final StartButtonsMenu startButtonsMenu = new StartButtonsMenu();

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
                message.setText(userData.messageToUser(update.getMessage().getChatId(), update.getMessage().getText()));
                setButtons(message, userData.getButtonsMenuStatus());
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                message.setChatId(System.getenv("MY_CHAT_ID_TELEGRAM"));
                message.setText("Ошибка telegram:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                message.setChatId(System.getenv("MY_CHAT_ID_TELEGRAM"));
                message.setText("Ошибка логики:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public synchronized void setButtons(SendMessage sendMessage, ButtonsMenuStatus buttonsMenuStatus) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        switch (buttonsMenuStatus) {
            case STARTMENU:
                for (int i = 0; i < startButtonsMenu.getCountRows(); i++) {
                    List<String> buttons = startButtonsMenu.getButtonsRow(i);
                    KeyboardRow keyboardRow = new KeyboardRow();
                    for (String button : buttons) {
                        keyboardRow.add(new KeyboardButton(button));
                    }
                    keyboard.add(keyboardRow);
                }
                break;
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
