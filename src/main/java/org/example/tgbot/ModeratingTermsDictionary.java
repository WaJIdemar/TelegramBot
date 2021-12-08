package org.example.tgbot;

import java.util.*;

public class ModeratingTermsDictionary {
    private final Map<Integer, TermDefinition> moderatingTermsDictionary= new HashMap<>();
    private final Integer upBound = 1000;
    private Integer currentIndex = 0;

    public Integer addNewTerm(String term, String definition) {
        moderatingTermsDictionary.put(currentIndex, new TermDefinition(term, definition));
        currentIndex++;
        if (currentIndex.equals(upBound)) {
            currentIndex = 0;
            return upBound;
        }
        return currentIndex - 1;
    }

    public TermDefinition getDefinition(Integer index) {
        return moderatingTermsDictionary.get(index);
    }
}
