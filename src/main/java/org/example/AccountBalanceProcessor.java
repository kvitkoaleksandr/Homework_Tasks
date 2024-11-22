package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AccountBalanceProcessor {
    public static void processFile(String inputFile, String outputFile) throws IOException {
        Map<String, List<Transaction>> transactionsByAccount = new HashMap<>();


        BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");

            double startingBalance = parts[0].trim().isEmpty() ? 0 : Double.parseDouble(parts[0].trim());
            String date = parts[1].trim();
            String accountNumber = parts[2].trim();
            double incoming = Double.parseDouble(parts[3].trim());
            double outgoing = Double.parseDouble(parts[4].trim());


            transactionsByAccount
                    .computeIfAbsent(accountNumber, k -> new ArrayList<>())
                    .add(new Transaction(startingBalance, date, incoming, outgoing));
        }


        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
        for (Map.Entry<String, List<Transaction>> entry : transactionsByAccount.entrySet()) {
            String accountNumber = entry.getKey();
            List<Transaction> transactions = entry.getValue();
            processAccountTransactions(accountNumber, transactions, writer);
        }

        writer.close();
    }

    private static void processAccountTransactions(String accountNumber, List<Transaction> transactions, BufferedWriter writer) throws IOException {
//        if (transactions.isEmpty()) {
//            return; // Если транзакций нет, выходим
//        }
        double currentBalance = calculateCurrentBalance(transactions.get(0)); // Начальный баланс
        String startDate = transactions.get(0).date; // Начальная дата
        String previousDate = startDate; // Предыдущая дата

        for (int i = 1; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);

            // Проверяем, есть ли изменения на счете
            if (transaction.incoming != 0 || transaction.outgoing != 0) {
                // Записываем период до дня изменений
                writePeriod(writer, startDate, previousDate, accountNumber, currentBalance);

                // Обновляем начальную дату нового периода
                startDate = transaction.date;

                // Обновляем баланс
                currentBalance = calculateCurrentBalance(transaction);
            }

            // Обновляем предыдущую дату
            previousDate = transaction.date;
        }

        // После цикла записываем последний период
        writePeriod(writer, startDate, previousDate, accountNumber, currentBalance);
    }


    private static double calculateCurrentBalance(Transaction transaction) {
        return transaction.startingBalance + transaction.incoming - transaction.outgoing;
    }


    private static void writePeriod(BufferedWriter writer, String startDate, String endDate, String accountNumber, double balance) throws IOException {
        writer.write(String.format("%s;%s;%s;%.2f%n", startDate, endDate, accountNumber, balance));
    }

    private static class Transaction {
        double startingBalance;
        String date;
        double incoming;
        double outgoing;

        Transaction(double startingBalance, String date, double incoming, double outgoing) {
            this.startingBalance = startingBalance;
            this.date = date;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }
    }
}