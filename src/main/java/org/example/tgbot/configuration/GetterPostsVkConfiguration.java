package org.example.tgbot.configuration;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.example.tgbot.appvk.GetterPostsVk;
import org.example.tgbot.appvk.GetterPostsVkDatabase;
import org.example.tgbot.appvk.GetterPostsVkStarter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class GetterPostsVkConfiguration {
    @Bean
    public GetterPostsVk getterPostsVk(){
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        Long adminGroupId = Long.parseLong(System.getenv("TELEGRAM_ADMIN_GROUP_ID"));
        int appVkId = Integer.parseInt(System.getenv("APP_VK_ID"));
        String appVkSecretKey = System.getenv("APP_VK_SECRET_KEY");
        String appVkServiceKey = System.getenv("APP_VK_SERVICE_KEY");
        int idVkGroup = Integer.parseInt(System.getenv("ID_VK_GROUP"));
        String accessGroupToken = System.getenv("APP_VK_ACCESS_GROUP_TOKEN");
        Long telegramChannelId = Long.parseLong(System.getenv("TELEGRAM_CHANNEL_ID"));
        return new GetterPostsVk(appVkId, appVkSecretKey, appVkServiceKey, idVkGroup, accessGroupToken, vk, getterPostsVkDatabase(),
                telegramBotConfiguration().chatClient(), telegramChannelId, adminGroupId);
    }

    @Bean
    public GetterPostsVkDatabase getterPostsVkDatabase(){
        String appVkTsId = System.getenv("APP_VK_TS_ID");
        String mongoUri = System.getenv("MONGO_URI");
        return new GetterPostsVkDatabase(mongoUri, appVkTsId);
    }

    @Bean
    public GetterPostsVkStarter getterPostsVkStarter(){
        return new GetterPostsVkStarter(getterPostsVk());
    }

    private TelegramBotConfiguration telegramBotConfiguration(){
        return new TelegramBotConfiguration();
    }
}
