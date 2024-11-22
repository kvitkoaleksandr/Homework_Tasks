package org.example.interviews;

import java.io.IOException;

public class MainTestNew {
    String inputFile = "in.txt";
    String outputFile = "out.txt";
    AccountBalanceProcessor account = new AccountBalanceProcessor();

    public void run() throws IOException {
        try {
            account.processFile(inputFile, outputFile);
            System.out.println("Обработка завершена. Результаты записаны в " + outputFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}