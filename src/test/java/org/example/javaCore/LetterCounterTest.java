package org.example.javaCore;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LetterCounterTest {

    LetterCounter letterCounter = new LetterCounter();

    private void assertVowelsAndConsonants(String input, int expectedVowels, int expectedConsonants) {
        Map<LetterType, Integer> result = letterCounter.countVowelsAndConsonants(input);
        assertEquals(expectedVowels, result.get(LetterType.VOWELS),
                "Гласных должно быть: " + expectedVowels + " для строки: \"" + input + "\"");
        assertEquals(expectedConsonants, result.get(LetterType.CONSONANTS),
                "Согласных должно быть: " + expectedConsonants + " для строки: \"" + input + "\"");
    }

    @Test
    void testCountVowelsAndConsonantsWordWithCapitalLetter() {
        assertVowelsAndConsonants("Hello", 2, 3);
    }

    @Test
    void testCountVowelsAndConsonantsEmpty() {
        assertVowelsAndConsonants("", 0, 0);
    }

    @Test
    void testCountVowelsAndConsonantsAllWordCapitalLetter() {
        assertVowelsAndConsonants("HELLO", 2, 3);
    }

    @Test
    void testCountVowelsAndConsonantsWordWithoutCapitalLetter() {
        assertVowelsAndConsonants("hello", 2, 3);
    }

    @Test
    void testCountVowelsAndConsonantsSentenceWithCharactersSpace() {
        assertVowelsAndConsonants("Hello world!", 3, 7);
    }

    @Test
    void testCountVowelsAndConsonantsWithNumbersAndSymbols() {
        assertVowelsAndConsonants("H3llo, W0rld!", 1, 7);
    }

    @Test
    void testCountVowelsAndConsonantsOnlyVowels() {
        assertVowelsAndConsonants("aeiouy", 6, 0);
    }

    @Test
    void testCountVowelsAndConsonantsOnlyConsonants() {
        assertVowelsAndConsonants("bcdfg", 0, 5);
    }
}