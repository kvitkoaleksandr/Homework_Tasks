package org.example.multithreading;

public class MailSender {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.out.println("Начало отправки писем...");

        // Создаем 5 потоков
        Thread thread1 = new Thread(new SenderRunnable(1, 200));
        Thread thread2 = new Thread(new SenderRunnable(201, 400));
        Thread thread3 = new Thread(new SenderRunnable(401, 600));
        Thread thread4 = new Thread(new SenderRunnable(601, 800));
        Thread thread5 = new Thread(new SenderRunnable(801, 1000));

        // Запускаем потоки
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        // Ждем завершения потоков
        joinThread(thread1);
        joinThread(thread2);
        joinThread(thread3);
        joinThread(thread4);
        joinThread(thread5);

        System.out.println("Все письма успешно отправлены!");
    }

    /**
     * Метод для безопасного вызова join()
     */
    private static void joinThread(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем статус прерывания
            System.err.println("Поток был прерван: " + e.getMessage());
        }
    }
}