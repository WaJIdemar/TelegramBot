package org.example.tgbot.databases;

import org.example.tgbot.databases.elements.ModeratorTermDefinition;

public interface ModeratingTermsDictionary {
    Long addNewTerm(String term, String definition, Long userId);

    ModeratorTermDefinition getModeratorTermDefinition(Long id);
}
