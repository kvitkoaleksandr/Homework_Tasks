package org.example.processingTestFiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVProcessor {

    public void calculateTotalSales(String inputFile, String outputFile) {
        double totalSales = 0.0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {

            String line = reader.readLine(); // Читаем заголовок
            while ((line = reader.readLine()) != null) {
                // Разделяем строку на части
                String[] parts = line.split(",");
                String product = parts[1]; // Продукт
                int quantity = Integer.parseInt(parts[2]); // Количество
                double price = Double.parseDouble(parts[3]); // Цена

                // Подсчитываем сумму для текущей строки
                double sales = quantity * price;
                totalSales += sales;
            }

            // Записываем общую сумму в файл
            writer.write("Общая сумма продаж: " + totalSales);
            System.out.println("Итог записан в " + outputFile);

        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }
}
