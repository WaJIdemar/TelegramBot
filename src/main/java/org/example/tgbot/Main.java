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
            String adminGroupId = System.getenv("TELEGRAM_ADMIN_GROUP_ID");
            Long moderatorGroupId = Long.parseLong(System.getenv("TELEGRAM_MODERATOR_GROUP_ID"));
            TelegramBot telegramBot = new TelegramBot(botName, botToken, adminGroupId);
            TelegramChatClient telegramChatClient = new TelegramChatClient(telegramBot);
            BotLogic botLogic = new BotLogic(telegramChatClient, moderatorGroupId, Long.parseLong(adminGroupId));
            telegramBot.setBotLogic(botLogic);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
