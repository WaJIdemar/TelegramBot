package org.tgbot.databases;

import org.tgbot.databases.elements.TermDefinition;

import java.util.ArrayList;

public interface TermsDictionary {
    void addNewTerm(TermDefinition termDefinition);

    TermDefinition getCertainDefinition(String term);

    Boolean containsTermOnDictionary(String term);

    ArrayList<String> searchSimilarTermsOnDictionary(String word);

    TermDefinition getRandomTerm();
}
