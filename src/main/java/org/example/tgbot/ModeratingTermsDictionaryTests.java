package org.example.tgbot;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ModeratingTermsDictionaryTests {
    private final ModeratingTermsDictionary moderatingTermsDictionary = new ModeratingTermsDictionary();

    @org.junit.jupiter.api.Test
    void addNewTermTest() {
        var index = moderatingTermsDictionary.addNewTerm("abrabra","blablabla", 234L);
        assertEquals(moderatingTermsDictionary.getDefinition(index).term, "abrabra");
        assertEquals(moderatingTermsDictionary.getDefinition(index).definition, "blablabla");
        assertEquals(moderatingTermsDictionary.get(index).userId, 234L);
    }
}
