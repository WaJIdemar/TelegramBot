package org.example.tgbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) throws TelegramApiException {
        String botName = System.getenv("TELEGRAM_BOT_NAME");
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        Long adminGroupId = Long.parseLong(System.getenv("TELEGRAM_ADMIN_GROUP_ID"));
        Long moderatorGroupId = Long.parseLong(System.getenv("TELEGRAM_MODERATOR_GROUP_ID"));
        Long telegramChannelId = Long.parseLong(System.getenv("TELEGRAM_CHANNEL_ID"));
        int idVkGroup = Integer.parseInt(System.getenv("ID_VK_GROUP"));
        String accessGroupToken = System.getenv("APP_VK_ACCESS_GROUP_TOKEN");
        int appVkId = Integer.parseInt(System.getenv("APP_VK_ID"));
        String appVkSecretKey = System.getenv("APP_VK_SECRET_KEY");
        String appVkServiceKey = System.getenv("APP_VK_SERVICE_KEY");
        String mongoUri = System.getenv("MONGO_URI");
        String appVkTsId = System.getenv("APP_VK_TS_ID");
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        TelegramBot telegramBot = new TelegramBot(botName, botToken, adminGroupId);
        TelegramChatClient telegramChatClient = new TelegramChatClient(telegramBot);
        BotLogic botLogic = new BotLogic(telegramChatClient, moderatorGroupId, adminGroupId, telegramChannelId,
                new TermsDictionaryRepo(mongoUri),
                new ModeratingTermsDictionaryRepo(mongoUri), new StandardResponses(), new StandardUserRequest(), new CallbackButton(),
                new DecisionOnTerm(), new UsersRepository(mongoUri));
        telegramBot.setBotLogic(botLogic);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        AppVkData appVkData = new AppVkData(mongoUri, appVkTsId);
        AppVk appVk = new AppVk(appVkId, appVkSecretKey, appVkServiceKey, idVkGroup, accessGroupToken, vk, appVkData,
                telegramChatClient, telegramChannelId, adminGroupId);
        Thread appVkThread = new Thread(appVk);
        appVkThread.start();
        botsApi.registerBot(telegramBot);
    }
}
