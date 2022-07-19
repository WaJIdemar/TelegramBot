package org.example.tgbot.tests;

import org.example.tgbot.databases.ModeratingTermsDictionary;
import org.example.tgbot.databases.elements.ModeratorTermDefinition;
import org.example.tgbot.databases.elements.TermDefinition;

import java.util.HashMap;
import java.util.Map;

public class TestModeratingTermsDictionary implements ModeratingTermsDictionary {
    private final Map<Long, ModeratorTermDefinition> moderatorTermsDictionary = new HashMap<>();
    private Long currentId;
    @Override
    public Long addNewTerm(String term, String definition, Long userId) {
        Long id = currentId;
        moderatorTermsDictionary.put(id, new ModeratorTermDefinition(new TermDefinition(term, definition), id, userId));
        currentId++;
        return id;
    }

    @Override
    public ModeratorTermDefinition getModeratorTermDefinition(Long id) {
        return moderatorTermsDictionary.get(id);
    }
}
