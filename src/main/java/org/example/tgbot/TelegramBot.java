package org.example.tgbot;

import javafx.util.Pair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelegramBot extends TelegramLongPollingBot {
    private final String Name;
    private final String Token;
    private BotLogic botLogic;
    private final String adminGroupId;

    public TelegramBot(String name, String token, String adminGroupId) {
        Name = name;
        Token = token;
        this.adminGroupId = adminGroupId;
    }

    public void setBotLogic(BotLogic botLogic) {
        this.botLogic = botLogic;
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
            try {
                botLogic.respondUser(update.getMessage().getChatId(), update.getMessage().getText());
            } catch (Exception e) {
                SendMessage message = new SendMessage();
                message.setChatId(adminGroupId);
                message.setText("Ошибка telegram-bot'a:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            try {
                EditMessageText et = new EditMessageText();
                et.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                et.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                et.setText(update.getCallbackQuery().getMessage().getText());
                botLogic.processingCallBack(update.getCallbackQuery().getData());
                execute(et);
            } catch (Exception e) {
                SendMessage message = new SendMessage();
                message.setChatId(adminGroupId);
                message.setText("Ошибка telegram-bot'a:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text, Keyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setReplayKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId);
            sendMessage.setText("Ошибка telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text, InlineKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId);
            sendMessage.setText("Ошибка telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void setReplayKeyboard(SendMessage sendMessage, Keyboard keyboard) {
        if (Objects.equals(keyboard, null)) {
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            return;
        }
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);


        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboardTelegram = new ArrayList<>();

        for (List<String> row : keyboard.buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : row)
                keyboardRow.add(new KeyboardButton(button));
            keyboardTelegram.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardTelegram);
    }

    private synchronized void setInlineKeyboard(SendMessage sendMessage, InlineKeyboard keyboard) {
        List<List<InlineKeyboardButton>> keyboardTelegram = new ArrayList<>();

        for (List<Pair<String, String>> row : keyboard.buttons) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (Pair<String, String> button : row) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(button.getKey());
                inlineKeyboardButton.setCallbackData(button.getValue());
                keyboardRow.add(inlineKeyboardButton);
            }
            keyboardTelegram.add(keyboardRow);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        inlineKeyboardMarkup.setKeyboard(keyboardTelegram);
    }
}

