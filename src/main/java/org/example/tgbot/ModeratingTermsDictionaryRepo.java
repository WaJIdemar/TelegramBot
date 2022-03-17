package org.example.tgbot;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.*;

public class ModeratingTermsDictionaryRepo {
    private final MongoCollection<ModeratorTermDefinition> moderatorTermsDictionary;
    private Long currentId;

    public ModeratingTermsDictionaryRepo(String mongoUri) {
        var codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                ClassModel.builder(ModeratorTermDefinition.class).conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION)).build()
                        ).automatic(true)
                        .build()));

        moderatorTermsDictionary = MongoClients.create(mongoUri).getDatabase("TopKube_TelegramBot")
                .withCodecRegistry(codecRegistry)
                .getCollection("ModeratorTermsDictionary", ModeratorTermDefinition.class);

        currentId = moderatorTermsDictionary.countDocuments();
    }

    public Long addNewTerm(String term, String definition, Long userId) {
        Long id = currentId;
        moderatorTermsDictionary.insertOne(new ModeratorTermDefinition(new TermDefinition(term, definition), id, userId));
        currentId++;
        return id;
    }

    public ModeratorTermDefinition getModeratorTermDefinition(Long id) {
        return moderatorTermsDictionary.find(Filters.eq("moderatorTermId", id)).first();
    }
}
