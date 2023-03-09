package org.tgbot.databases;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.tgbot.LevenshteinCalculator;
import org.tgbot.databases.elements.TermDefinition;

import java.util.ArrayList;
import java.util.List;

public class TermsDictionaryRepo implements TermsDictionary {
    private final MongoCollection<TermDefinition> termsDictionary;
    private final LevenshteinCalculator levenshteinCalculator = new LevenshteinCalculator();

    public TermsDictionaryRepo(String mongoUri) {
        var codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                ClassModel.builder(TermDefinition.class).conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION)).build()
                        ).automatic(true)
                        .build()));

        termsDictionary = MongoClients.create(mongoUri).getDatabase("TopKube_TelegramBot")
                .withCodecRegistry(codecRegistry)
                .getCollection("TermsDictionary", TermDefinition.class);
    }

    public void addNewTerm(TermDefinition termDefinition) {
        termsDictionary.insertOne(termDefinition);
    }

    public TermDefinition getCertainDefinition(String term) {
        return termsDictionary.find(Filters.eq("term", term)).first();
    }

    public Boolean containsTermOnDictionary(String term) {
        return termsDictionary.find(Filters.eq("term", term)).first() != null;
    }

    public ArrayList<String> searchSimilarTermsOnDictionary(String word) {
        var minWords = new ArrayList<String>();
        var list = termsDictionary.find();
        for (TermDefinition termDefinition : list) {
            var levenshtein = levenshteinCalculator.levenshteinDistance(termDefinition.term, word);
            if (levenshtein < 3) {
                minWords.add(termDefinition.term);
            }
        }
        return minWords;
    }

    public TermDefinition getRandomTerm() {
        var list = termsDictionary.aggregate(List.of(Aggregates.sample(1)));
        return list.first();
    }
}
