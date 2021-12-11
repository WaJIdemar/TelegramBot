package org.example.tgbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
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
            String telegramChannelId = System.getenv("TELEGRAM_CHANNEL_ID");
            int idVkGroup = Integer.parseInt(System.getenv("ID_VK_GROUP"));
            String accessTokenVk = System.getenv("APP_VK_ACCESS_TOKEN");
            TransportClient transportClient = new HttpTransportClient();
            VkApiClient vk = new VkApiClient(transportClient);
            TelegramBot telegramBot = new TelegramBot(botName, botToken, adminGroupId, telegramChannelId);
            TelegramChatClient telegramChatClient = new TelegramChatClient(telegramBot);
            BotLogic botLogic = new BotLogic(telegramChatClient, moderatorGroupId, Long.parseLong(adminGroupId), new TermsDictionary(),
                    new ModeratingTermsDictionary(), new StandardResponsesToUser(), new StandardUserRequest(), new CallbackButton(),
                    new DecisionOnTerm());
            telegramBot.setBotLogic(botLogic);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            AppVk appVk = new AppVk(idVkGroup, accessTokenVk, vk, telegramChatClient);
            Thread appVkThread = new Thread(appVk);
            appVkThread.start();
            botsApi.registerBot(telegramBot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
