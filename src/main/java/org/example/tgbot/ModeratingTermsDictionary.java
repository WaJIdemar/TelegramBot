package org.example.tgbot;

import java.util.*;

public class ModeratingTermsDictionary {
    private final Map<Integer, TermDefinition> moderatingTermsDictionary= new HashMap<>();
    private final Integer upBound = 1000;
    private Integer currentIndex = 0;

    public Integer addNewTerm(String term, String definition) {
        Integer index = currentIndex;
        moderatingTermsDictionary.put(index, new TermDefinition(term, definition));
        currentIndex++;
        if (currentIndex.equals(upBound)) {
            currentIndex = 0;
        }
        return index;
    }

    public TermDefinition getDefinition(Integer index) {
        return moderatingTermsDictionary.get(index);
    }
}
