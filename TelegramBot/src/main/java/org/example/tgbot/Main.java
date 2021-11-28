package org.example.tgbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) {
        try {
            String botName = System.getenv("TELEGRAM_BOT_NAME");
            String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
            String adminChatId = System.getenv("MY_CHAT_ID_TELEGRAM");
            String telegramGroupChatId = "-1001683570202";
            TelegramBot telegramBot = new TelegramBot(botName, botToken, adminChatId, telegramGroupChatId);
            TelegramChatClient telegramChatClient = new TelegramChatClient(telegramBot);
            BotLogic botLogic = new BotLogic(telegramChatClient);
            telegramBot.setBotLogic(botLogic);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
