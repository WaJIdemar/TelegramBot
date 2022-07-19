package org.example.tgbot.appvk;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.List;

public class GetterPostsVkDatabase {
    private final MongoCollection<AppVkTs> appVkTsData;
    private final String appVkTsId;

    public GetterPostsVkDatabase(String mongoUri, String appVkTsId) {
        var codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                ClassModel.builder(AppVkTs.class).conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION)).build()
                        ).automatic(true)
                        .build()));

        appVkTsData = MongoClients.create(mongoUri).getDatabase("TopKube_TelegramBot")
                .withCodecRegistry(codecRegistry)
                .getCollection("Ts", AppVkTs.class);
        this.appVkTsId = appVkTsId;
    }

    public AppVkTs getAppVkTs() {
        return appVkTsData.find().first();
    }

    public void changeAppVkTs(AppVkTs newAppVkTs) {
        appVkTsData.findOneAndReplace(new Document("_id", new ObjectId(appVkTsId)), newAppVkTs);
    }
}
