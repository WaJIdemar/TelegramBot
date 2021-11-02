package org.example.tgbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            Integer appVkId = Integer.parseInt(System.getenv("APP_VK_ID"));
            String appVkSecretKey = System.getenv("APP_VK_SECRET_KEY");
            String appVkAccessToken = System.getenv("APP_VK_ACCESS_TOKEN");
            Integer idVkGroup = Integer.parseInt(System.getenv("ID_VK_GROUP"));
            TransportClient transportClient = HttpTransportClient.getInstance();
            AppVk appVk = new AppVk(appVkId, appVkSecretKey, appVkAccessToken, idVkGroup, transportClient);

            String botName = System.getenv("TELEGRAM_BOT_NAME");
            String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
            BotLogic botLogic = new BotLogic();

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot(botName, botToken, botLogic, appVk));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
