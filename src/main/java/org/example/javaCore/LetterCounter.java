package org.example.javaCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LetterCounter {
    // Списки гласных и согласных, но теперь они содержат символы (char), а не строки (String)
    private static final List<Character> VOWELS = List.of('a', 'e', 'i', 'o', 'u', 'y');
    private static final List<Character> CONSONANTS = List.of(
            'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k',
            'l', 'm', 'n', 'p', 'q', 'r', 's', 't',
            'v', 'w', 'x', 'z'
    );

    public void processAndShow(String input) {
        if (input == null || input.isEmpty()) {
            System.out.println("Пустая строка! Нет данных для подсчёта.");
            return;
        }
        Map<LetterType, Integer> i = countVowelsAndConsonants(input);
        showWords(i);
    }

    public Map<LetterType, Integer> countVowelsAndConsonants(String input) {
        int vowelsCount = 0;
        int consonantsCount = 0;

        for (char c : input.toLowerCase().toCharArray()) { // Преобразуем строку в нижний регистр
            if (VOWELS.contains(c)) {
                vowelsCount++;
            } else if (CONSONANTS.contains(c)) {
                consonantsCount++;
            }
        }

        Map<LetterType, Integer> result = new HashMap<>();
        result.put(LetterType.VOWELS, vowelsCount);
        result.put(LetterType.CONSONANTS, consonantsCount);
        return result;
    }

    public void showWords(Map<LetterType, Integer> result) {
        System.out.println("Гласных: " + result.get(LetterType.VOWELS));
        System.out.println("Согласных: " + result.get(LetterType.CONSONANTS));
    }
}