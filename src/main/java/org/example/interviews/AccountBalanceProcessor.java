package org.example.interviews;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        double currentBalance = 0; // остаток на балансе
        String startDate = null; // Начальная дата
        String finishDate = null; // конечная дата

        for (int i = 0; i < transactions.size(); i++) {
            //Transaction transaction = transactions.get(i);
            if (i == 0) {
                currentBalance = currentBalance + calculateCurrentBalance(transactions.get(i));
                startDate = transactions.get(i).date;
                finishDate = startDate;
            } else if (transactions.get(i).incoming != 0 || transactions.get(i).outgoing != 0) {
                // Записываем период до дня изменений
                writePeriod(writer, startDate, finishDate, accountNumber, currentBalance);

                // Обновляем начальную и конечную дату нового периода
                startDate = transactions.get(i).date;
                finishDate = startDate;
                // Обновляем баланс
                currentBalance = currentBalance + calculateCurrentBalance(transactions.get(i));
            } else {
                finishDate = transactions.get(i).date;
            }
        }
        // После цикла записываем последний период
        writePeriod(writer, startDate, finishDate, accountNumber, currentBalance);
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