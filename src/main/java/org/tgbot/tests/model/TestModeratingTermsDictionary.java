package org.tgbot.tests.model;

import org.tgbot.databases.ModeratingTermsDictionary;
import org.tgbot.databases.elements.ModeratorTermDefinition;
import org.tgbot.databases.elements.TermDefinition;

import java.util.HashMap;
import java.util.Map;

public class TestModeratingTermsDictionary implements ModeratingTermsDictionary {
    private final Map<Long, ModeratorTermDefinition> moderatorTermsDictionary = new HashMap<>();
    private Long currentId = 0L;
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

    public boolean isEmpty() {
        return moderatorTermsDictionary.isEmpty();
    }

    public void clear() {
        moderatorTermsDictionary.clear();
        currentId = 0L;
    }
}
