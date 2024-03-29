package org.tgbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tgbot.buttons.InlineButton;
import org.tgbot.keyboards.CallbackKeyboard;
import org.tgbot.keyboards.Keyboard;
import org.tgbot.keyboards.UrlKeyboard;
import org.tgbot.logic.BotLogic;
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
    private static final Logger log = LogManager.getLogger(TelegramBot.class);

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
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                log.info("Receive new Update. updateID: " + update.getUpdateId() + " Chat ID: " + update.getMessage().getChatId()
                        + " Text: " + update.getMessage().getText());
                botLogic.respondUser(update.getMessage().getChatId(), update.getMessage().getText());

            } else if (update.hasCallbackQuery()) {
                log.info("Receive new Callback. updateID: " + update.getUpdateId() + " Chat ID: " + update.getMessage().getChatId()
                        + " Data: " + update.getCallbackQuery().getData());
                botLogic.processingCallBack(update.getCallbackQuery().getMessage().getChatId(),
                        update.getCallbackQuery().getMessage().getMessageId(),
                        update.getCallbackQuery().getMessage().getText(), update.getCallbackQuery().getData());
            }
        } catch (Exception e) {
            sendErrorMessage(e);
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

    public void sendMessage(Long chatId, String text, List<String> photosUrl, UrlKeyboard keyboard) {
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
            sendErrorMessage(e);
        }
    }

    public void sendMessage(Long chatId, String text, String photoUrl, UrlKeyboard keyboard) {
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
            sendErrorMessage(e);
        }
    }

    public void sendMessage(Long chatId, String text, UrlKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setUrlInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void sendMessage(Long chatId, String text, Keyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setReplayKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void sendMessage(Long chatId, String text, CallbackKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            setCallbackInlineKeyboard(sendMessage, keyboard);
            execute(sendMessage);
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        try {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            execute(sendMessage);
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    private void setReplayKeyboard(SendMessage sendMessage, Keyboard keyboard) {
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

    public void editMessage(Long chatId, Integer messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        try {
            editMessageText.setChatId(chatId.toString());
            editMessageText.setMessageId(messageId);
            editMessageText.setText(text);
            execute(editMessageText);
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    private void sendErrorMessage(Exception exception) {
        log.error(exception);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminGroupId.toString());
        sendMessage.setText("Ошибка telegram-bot'a:\n" + exception);
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            log.error(exception);
            ex.printStackTrace();
        }
    }

    private void setUrlInlineKeyboard(SendMessage sendMessage, UrlKeyboard keyboard) {
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

    private void setCallbackInlineKeyboard(SendMessage sendMessage, CallbackKeyboard keyboard) {
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

