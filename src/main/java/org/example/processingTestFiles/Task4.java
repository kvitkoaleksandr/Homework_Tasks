package org.example.processingTestFiles;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Task4 {

    // Метод для фильтрации строк с ключевым словом
    public void filterLinesWithKeyword(String inputFile, String outputFile, String keyword) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Фильтрация завершена. Результат записан в " + outputFile);
        }
    }

    // Метод для подсчёта частоты встречаемости слов
    public Map<String, Integer> countWordFrequency(String inputFile) throws IOException {
        Map<String, Integer> wordFrequency = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+"); // Разделяем строку на слова
                for (String word : words) {
                    if (!word.isEmpty()) {
                        word = word.toLowerCase(); // Приводим к нижнему регистру для унификации
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        return wordFrequency;
    }
}