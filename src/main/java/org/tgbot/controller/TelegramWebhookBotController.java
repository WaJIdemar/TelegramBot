package org.tgbot.controller;

import org.tgbot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class TelegramWebhookBotController {
    private final TelegramBot telegramBot;

    @Autowired
    public TelegramWebhookBotController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceive(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
