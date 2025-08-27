package org.example.task;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainCrptApi {
//    public static void main(String[] args) {
//        try {
//            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
//            System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
//        } catch (java.io.UnsupportedEncodingException ignored) {}
//
//        HttpServer server = null;
//        try {
//            server = HttpServer.create(new InetSocketAddress(0), 0);
//            server.createContext("/lk/documents/create", ex -> {
//                String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//                System.out.println("Сервер: получен запрос, длина тела = " + body.length() + " байт");
//                String id = UUID.randomUUID().toString();
//                byte[] ok = ("{\"value\":\"" + id + "\"}").getBytes(StandardCharsets.UTF_8);
//                ex.getResponseHeaders().add("Content-Type", "application/json");
//                ex.sendResponseHeaders(200, ok.length);
//                try (OutputStream os = ex.getResponseBody()) { os.write(ok); }
//            });
//            server.start();
//
//            URI base = URI.create("http://localhost:" + server.getAddress().getPort());
//            System.out.println("Локальный тестовый сервер запущен: " + base);
//
//            // 2) Клиент с лимитом 1 запрос/сек
//            CrptApi api = new CrptApi(TimeUnit.SECONDS, 1, base, () -> "demo-token");
//
//            // 3) Минимальный документ
//            CrptApi.Document doc = new CrptApi.Document();
//            doc.participant_inn = "1234567890";
//            doc.producer_inn = "1234567890";
//            doc.production_date = "2025-08-25";
//            doc.production_type = "OWN_PRODUCTION";
//            CrptApi.Document.Product p = new CrptApi.Document.Product();
//            p.owner_inn = "1234567890";
//            p.producer_inn = "1234567890";
//            p.production_date = "2025-08-25";
//            p.tnved_code = "00000000";
//            p.uit_code = "00000000000000000000000000000000000000000000000000";
//            doc.products = List.of(p);
//
//            // 4) Первый вызов — должен пройти сразу
//            System.out.println("Отправляем первый документ...");
//            CrptApi.DocumentId id1 = api.createIntroduceGoods(doc, "SIG-EXAMPLE", "milk");
//            System.out.println("Успех: документ создан, id = " + id1.value);
//
//            // 5) Второй вызов — должен подождать ~1 секунду (лимит 1/сек)
//            System.out.println("Отправляем второй документ (должен подождать из-за лимита)...");
//            long t0 = System.nanoTime();
//            CrptApi.DocumentId id2 = api.createIntroduceGoods(doc, "SIG-EXAMPLE", "milk");
//            long ms = Duration.ofNanos(System.nanoTime() - t0).toMillis();
//            System.out.println("Успех: документ создан, id = " + id2.value);
//            System.out.println("Время между вызовами ≈ " + ms + " мс (ограничение сработало)");
//
//            // 6) Пример ожидаемой ошибки валидации токена
//            try {
//                System.out.println("Пробуем вызвать с пустым токеном (ожидаем ошибку)...");
//                CrptApi bad = new CrptApi(TimeUnit.SECONDS, 5, base, () -> "");
//                bad.createIntroduceGoods(doc, "SIG-EXAMPLE", "milk");
//            } catch (IllegalArgumentException iae) {
//                System.out.println("Ожидаемая ошибка: " + iae.getMessage());
//            }
//
//            System.out.println("Демонстрация завершена ✅");
//        } catch (IOException e) {
//            System.err.println("Не удалось запустить локальный сервер: " + e.getMessage());
//        } finally {
//            if (server != null) {
//                server.stop(0);
//                System.out.println("Сервер остановлен.");
//            }
//        }
//    }
}