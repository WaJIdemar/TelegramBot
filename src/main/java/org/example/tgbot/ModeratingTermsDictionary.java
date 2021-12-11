package org.example.tgbot;

import java.util.*;

public class ModeratingTermsDictionary {
    private final Map<Integer, ModeratorTermDefinition> moderatingTermsDictionary= new HashMap<>();
    private Integer currentIndex = 0;

    public Integer addNewTerm(String term, String definition, Long userId) {
        Integer id = currentIndex;
        moderatingTermsDictionary.put(id, new ModeratorTermDefinition(new TermDefinition(term, definition), userId));
        currentIndex++;
        return id;
    }

    public TermDefinition getDefinition(Integer index) {
        return moderatingTermsDictionary.get(index).termDefinition;
    }

    public ModeratorTermDefinition get(Integer key){
        return moderatingTermsDictionary.get(key);
    }
}
