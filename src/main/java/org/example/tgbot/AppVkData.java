package org.example.tgbot;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.client.model.Filters;

import java.util.List;

public class AppVkData {
    private MongoCollection<AppVkTs> appVkTs;

    public AppVkData(String mongoUri) {
        var codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                ClassModel.builder(AppVkTs.class).conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION)).build()
                        ).automatic(true)
                        .build()));

        appVkTs = MongoClients.create(mongoUri).getDatabase("Ts")
                .withCodecRegistry(codecRegistry)
                .getCollection("Ts", AppVkTs.class);
    }

    public AppVkTs getAppVkTs(){
        return appVkTs.find().first();
    }
    public void changeAppVkTs(AppVkTs newAppVkTs){
        AppVkTs oldAppVkTs = getAppVkTs();
        appVkTs.updateOne(Filters.eq("ts", oldAppVkTs.getTs()), new Document("$set", newAppVkTs),
                new UpdateOptions().upsert(true));
    }
}
