package org.example.tgbot.configuration;

import org.example.tgbot.*;
import org.example.tgbot.buttons.CallbackButton;
import org.example.tgbot.databases.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class TelegramBotConfiguration {

    @Bean
    public TelegramBot telegramBot(){
        String botName = System.getenv("TELEGRAM_BOT_NAME");
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        Long adminGroupId = Long.parseLong(System.getenv("TELEGRAM_ADMIN_GROUP_ID"));
        return new TelegramBot(botName, botToken, adminGroupId);
    }

    @Bean
    public BotLogic botLogic(){
        Long adminGroupId = Long.parseLong(System.getenv("TELEGRAM_ADMIN_GROUP_ID"));
        Long moderatorGroupId = Long.parseLong(System.getenv("TELEGRAM_MODERATOR_GROUP_ID"));
        Long telegramChannelId = Long.parseLong(System.getenv("TELEGRAM_CHANNEL_ID"));
        ChatClient telegramChatClient = chatClient();

        BotLogic botLogic = new BotLogic(telegramChatClient, moderatorGroupId, adminGroupId, telegramChannelId,
                termsDictionary(), moderatingTermsDictionary(), standardResponses(), standardUserRequest(), callbackButton(),
                decisionOnTerm(), databaseUsers());
        telegramBot().setBotLogic(botLogic);
        return botLogic;
    }

    @Bean
    public ChatClient chatClient(){
        return new TelegramChatClient(telegramBot());
    }

    @Bean
    public TermsDictionary termsDictionary(){
        return new TermsDictionaryRepo(System.getenv("MONGO_URI"));
    }

    @Bean
    public ModeratingTermsDictionary moderatingTermsDictionary(){
        return new ModeratingTermsDictionaryRepo(System.getenv("MONGO_URI"));
    }

    @Bean
    public DatabaseUsers databaseUsers(){
        return new UsersRepository(System.getenv("MONGO_URI"));
    }


    @Bean
    public StandardResponses standardResponses(){
        return new StandardResponses();
    }

    @Bean
    public StandardUserRequest standardUserRequest(){
        return new StandardUserRequest();
    }

    @Bean
    public CallbackButton callbackButton(){
        return new CallbackButton();
    }

    @Bean DecisionOnTerm decisionOnTerm(){
        return new DecisionOnTerm();
    }
}
