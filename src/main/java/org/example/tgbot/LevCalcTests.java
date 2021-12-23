package org.example.tgbot;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LevCalcTests {
    private final LevenshteinCalculator levenshteinCalculator = new LevenshteinCalculator();

    @org.junit.jupiter.api.Test
    void sameLength() {
        assertEquals(0,levenshteinCalculator.levenshteinDistance("a", "a"));
    }

    @org.junit.jupiter.api.Test
    void firstLengthLess() {
        assertEquals(4,levenshteinCalculator.levenshteinDistance("a", "aaaav"));
    }

    @org.junit.jupiter.api.Test
    void firstLengthMore() {
        assertEquals(3,levenshteinCalculator.levenshteinDistance("aabca", "aa"));
    }
}
