package org.tgbot.databases;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.tgbot.databases.elements.User;

import java.util.List;

public class UsersRepository implements DatabaseUsers {
    private final MongoCollection<User> users;

    public UsersRepository(String mongoUri) {
        var codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                ClassModel.builder(User.class).conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION)).build()
                        ).automatic(true)
                        .build()));

        users = MongoClients.create(mongoUri).getDatabase("TopKube_TelegramBot")
                .withCodecRegistry(codecRegistry)
                .getCollection("Users", User.class);
    }

    public User getUserOrCreate(Long chatId) {
        var user = users.find(Filters.eq("userId", chatId)).first();
        return user == null ? new User(chatId) : user;
    }

    public void put(User user) {
        users.updateOne(Filters.eq("userId", user.getUserId()), new Document("$set", user),
                new UpdateOptions().upsert(true));
    }
}
