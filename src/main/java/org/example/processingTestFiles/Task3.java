package org.example.processingTestFiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Task3 {

    public String convertJSONToCSV(String inputFile) throws IOException {
        // Создаём объект ObjectMapper для работы с JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Читаем JSON-файл и преобразуем его в список карт (List<Map<String, Object>>)
        List<Map<String, Object>> users = objectMapper.readValue(new File(inputFile), new TypeReference<>() {});

        if (users.isEmpty()) {
            return ""; // Если JSON пуст, возвращаем пустую строку
        }

        // Формируем заголовок
        String headers = String.join(",", users.get(0).keySet());

        // Формируем строки данных
        String rows = users.stream()
                .map(user -> user.values().stream()
                        .map(String::valueOf) // Преобразуем значения в строки
                        .collect(Collectors.joining(","))) // Объединяем значения через запятую
                .collect(Collectors.joining("\n")); // Разделяем строки новой строкой

        // Возвращаем весь CSV как одну строку
        return headers + "\n" + rows;
    }
}