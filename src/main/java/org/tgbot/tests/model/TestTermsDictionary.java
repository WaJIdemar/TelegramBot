package org.tgbot.tests.model;

import org.tgbot.LevenshteinCalculator;
import org.tgbot.databases.TermsDictionary;
import org.tgbot.databases.elements.TermDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestTermsDictionary implements TermsDictionary {
    private final LevenshteinCalculator levenshteinCalculator = new LevenshteinCalculator();
    private final Map<String, TermDefinition> termsDictionary = new HashMap<>();
    @Override
    public void addNewTerm(TermDefinition termDefinition) {
        termsDictionary.put(termDefinition.term, termDefinition);
    }

    @Override
    public TermDefinition getCertainDefinition(String term) {
        return termsDictionary.get(term);
    }

    @Override
    public Boolean containsTermOnDictionary(String term) {
        return termsDictionary.containsKey(term);
    }

    @Override
    public ArrayList<String> searchSimilarTermsOnDictionary(String word) {
        var minWords = new ArrayList<String>();
        for (TermDefinition termDefinition : termsDictionary.values()) {
            var levenshtein = levenshteinCalculator.levenshteinDistance(termDefinition.term, word);
            if (levenshtein < 3) {
                minWords.add(termDefinition.term);
            }
        }
        return minWords;
    }

    @Override
    public TermDefinition getRandomTerm() {
        return new TermDefinition("test", "test");
    }

    public boolean isEmpty(){
        return termsDictionary.isEmpty();
    }

    public void clear() {
        termsDictionary.clear();
    }
}
