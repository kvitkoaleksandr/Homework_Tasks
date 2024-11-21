package org.example.task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;



public class Task1 {


    public String processStudents(String inputFile) throws Exception {
        String outputFile = inputFile.replace(".txt", "_filtered.txt");

        try {
            // Чтение строк из файла
            List<String> lines = Files.readAllLines(Paths.get(inputFile));

            // Обработка данных: фильтрация и сортировка
            List<String> filteredStudents = lines.stream()
                    .map(line -> line.split(":")) // Разделяем на имя и баллы
                    .filter(parts -> parts.length == 2) // Убедимся, что строка корректна
                    .map(parts -> new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1]))) // Преобразуем в пары (имя, баллы)
                    .filter(entry -> entry.getValue() >= 80) // Фильтруем студентов с баллами >= 80
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Сортируем по баллам в порядке убывания
                    .map(entry -> entry.getKey() + ":" + entry.getValue()) // Обратно преобразуем в строку
                    .collect(Collectors.toList());

            // Запись результатов в новый файл
            Files.write(Paths.get(outputFile), filteredStudents);

            System.out.println("Фильтрация завершена. Результат записан в " + outputFile);
        } catch (IOException e) {
            throw new Exception("Ошибка при обработке файла: " + e.getMessage(), e);
        }
        return outputFile;
    }
}