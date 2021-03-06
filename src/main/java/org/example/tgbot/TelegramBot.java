package org.example.tgbot;

import org.example.tgbot.buttons.InlineButton;
import org.example.tgbot.keyboards.CallbackKeyboard;
import org.example.tgbot.keyboards.Keyboard;
import org.example.tgbot.keyboards.UrlKeyboard;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
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

public class TelegramBot extends TelegramWebhookBot {
    private final String name;
    private final String token;
    private final String botPath;
    private final Long adminGroupId;
    private BotLogic botLogic;

    public TelegramBot(String name, String token, String botPath, Long adminGroupId) {
        this.name = name;
        this.token = token;
        this.adminGroupId = adminGroupId;
        this.botPath = botPath;
    }

    public void setBotLogic(BotLogic botLogic) {
        this.botLogic = botLogic;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                botLogic.respondUser(update.getMessage().getChatId(), update.getMessage().getText());
            } catch (Exception e) {
                SendMessage message = new SendMessage();
                message.setChatId(adminGroupId.toString());
                message.setText("???????????? telegram-bot'a:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            try {
                botLogic.processingCallBack(update.getCallbackQuery().getMessage().getChatId(),
                        update.getCallbackQuery().getMessage().getMessageId(),
                        update.getCallbackQuery().getMessage().getText(), update.getCallbackQuery().getData());
            } catch (Exception e) {
                SendMessage message = new SendMessage();
                message.setChatId(adminGroupId.toString());
                message.setText("???????????? telegram-bot'a:\n" + e);
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public synchronized void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        try {
            List<InputMedia> photos = new ArrayList<>();
            for (String url : photosUrl) {
                InputMedia inputMediaPhoto = new InputMediaPhoto();
                inputMediaPhoto.setMedia(url);
                photos.add(inputMediaPhoto);
            }
            sendMediaGroup.setChatId(chatId.toString());
            sendMediaGroup.setMedias(photos);
            execute(sendMediaGroup);
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setUrlInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text, String photoUrl, UrlKeyboard keyboard) {
        SendPhoto sendPhoto = new SendPhoto();
        SendMessage sendMessage = new SendMessage();
        try {
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile().setMedia(photoUrl));
            execute(sendPhoto);
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setUrlInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text, UrlKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setUrlInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
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
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text, CallbackKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setCallbackInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            execute(sendMessage);
        } catch (Exception e) {
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
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
        // ?????????????? ??????????????????????
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // ?????????????? ???????????? ?????????? ????????????????????
        List<KeyboardRow> keyboardTelegram = new ArrayList<>();

        for (List<String> row : keyboard.buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : row)
                keyboardRow.add(new KeyboardButton(button));
            keyboardTelegram.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardTelegram);
    }

    public synchronized void editMessage(Long chatId, Integer messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        try {
            editMessageText.setChatId(chatId.toString());
            editMessageText.setMessageId(messageId);
            editMessageText.setText(text);
            execute(editMessageText);
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(adminGroupId.toString());
            sendMessage.setText("???????????? telegram-bot'a:\n" + e);
            try {
                execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void setUrlInlineKeyboard(SendMessage sendMessage, UrlKeyboard keyboard) {
        List<List<InlineKeyboardButton>> keyboardTelegram = new ArrayList<>();

        for (List<InlineButton> row : keyboard.buttons) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (InlineButton button : row) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(button.text);
                inlineKeyboardButton.setUrl(button.data);
                keyboardRow.add(inlineKeyboardButton);
            }
            keyboardTelegram.add(keyboardRow);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        inlineKeyboardMarkup.setKeyboard(keyboardTelegram);
    }

    private synchronized void setCallbackInlineKeyboard(SendMessage sendMessage, CallbackKeyboard keyboard) {
        List<List<InlineKeyboardButton>> keyboardTelegram = new ArrayList<>();

        for (List<InlineButton> row : keyboard.buttons) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (InlineButton button : row) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(button.text);
                inlineKeyboardButton.setCallbackData(button.data);
                keyboardRow.add(inlineKeyboardButton);
            }
            keyboardTelegram.add(keyboardRow);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        inlineKeyboardMarkup.setKeyboard(keyboardTelegram);
    }
}

