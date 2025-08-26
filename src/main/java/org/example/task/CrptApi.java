package org.example.task;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Deque;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CrptApi {
    private final URI baseUri; // Базовый адрес API (prod/demo); не меняется после создания
    private final HttpClient http; // Переиспользуемый HTTP-клиент JDK; потокобезопасен, с пулом соединений
    private final ObjectMapper json; // Jackson-сериализатор/десериализатор JSON; общий, потокобезопасный
    private final TokenProvider tokenProvider; // Абстракция получения Bearer-токена (DI), можно подменять/обновлять
    private final RateLimiter limiter; // Блокирующий rate-limiter (N req / окно), общий для всех потоков
    private final Clock clock; // Источник времени (DI) для измерений/таймингов; сейчас передаётся в лимитер

    // ===== Public API =====
    // Источник Bearer-токена (DI). Позволяет подменять способ получения/обновления токена без изменения CrptApi.
    public interface TokenProvider {
        String getToken();

    }
    public static final class DocumentId { // public — доступен извне; static — вложенный класс не привязан к экземпляру CrptApi; final — запрещено наследование (stable value object). Не «константа».
        public final String value;


        public DocumentId(String value) {
            if (value == null) { // Явная проверка на null с понятным сообщением; объект создаётся только с непустым id.
                throw new NullPointerException("value is null");
            }
            this.value = value;
        }
        @Override
        public String toString() {
            return value;
        }

    }
    // Document — это простая модель (POJO), которую мы отправляем в Честный Знак. Поля названы так же,
    // как в спецификации API (snake_case), поэтому Jackson без дополнительных аннотаций превратит объект в корректный JSON.
    public static class Document {
        // participant_inn, producer_inn, production_date, production_type — «шапка» документа:
        // ИНН участника/производителя, дата производства (строкой YYYY-MM-DD, чтобы не усложнять форматирование дат),
        // и тип производства (OWN_PRODUCTION/CONTRACT_PRODUCTION).
        //products — список товарных позиций.
        public String participant_inn;
        public String producer_inn;
        public String production_date;   // YYYY-MM-DD
        public String production_type;   // OWN_PRODUCTION | CONTRACT_PRODUCTION
        public java.util.List<Product> products;

        //Product — одна позиция товара. Поля соответствуют схеме API: данные сертификата,
        // ИНН владельца/производителя, дата производства, tnved_code, и один из кодов маркировки uit_code/uitu_code.
        public static class Product {
            public String certificate_document;
            public String certificate_document_date;
            public String certificate_document_number;
            public String owner_inn;
            public String producer_inn;
            public String production_date; // YYYY-MM-DD
            public String tnved_code;
            public String uit_code;
            public String uitu_code;
            //Итог: эти классы — минимальная и прозрачная модель полезной нагрузки для типа LP_INTRODUCE_GOODS.
            // Они существуют здесь только чтобы было удобно собрать валидное тело запроса и сериализовать его
            // «как в документации» без лишней магии.
        }

    }
    // Обработку исключений ещё не добавил
    // ApiException — любые «клиентские» 4xx-ошибки от CRPT, кроме 401/403; сообщение берётся из error_message.
    public static class ApiException extends RuntimeException {
        public ApiException(String m) {
            super(m);
        }
    }
    // AuthException — отдельный тип для 401/403 (проблемы аутентификации/прав), чтобы вызывать особую обработку
    // (обновить токен и т.п.).
    public static class AuthException extends RuntimeException {
        public AuthException(String m) {
            super(m);
        }
    }
    // TransportException — сетевые/низкоуровневые сбои и 5xx после ретрая; есть конструктор с cause.
    public static class TransportException extends RuntimeException {

        public TransportException(String m, Throwable c) {
            super(m, c);
        }
        public TransportException(String m) {
            super(m);
        }
    }
    // RateLimitInterruptedException — поток прервали во время ожидания в rate-лимитере (мы сохраняем флаг interrupt).
    public static class RateLimitInterruptedException extends RuntimeException {
        public RateLimitInterruptedException(String m) {
            super(m);
        }

    }
    // ===== State =====
    //private static final Logger LOG = Logger.getLogger(CrptApi.class.getName());


    public CrptApi(TimeUnit unit, int requestLimit, TokenProvider tokenProvider) {
        this(unit, requestLimit, URI.create("https://ismp.crpt.ru/api/v3"), tokenProvider);
    }

    public CrptApi(TimeUnit unit, int requestLimit, URI baseUri, TokenProvider tokenProvider) {
        this(unit, requestLimit, baseUri, tokenProvider,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(),
                defaultMapper(),
                Clock.systemUTC());
    }

    CrptApi(TimeUnit unit,
            int requestLimit,
            URI baseUri,
            TokenProvider tokenProvider,
            HttpClient http,
            ObjectMapper mapper,
            Clock clock) {
        if (unit == null) throw new IllegalArgumentException("timeUnit is null");
        if (requestLimit <= 0) throw new IllegalArgumentException("requestLimit must be > 0");
        this.baseUri = Objects.requireNonNull(baseUri, "baseUri");
        this.tokenProvider = Objects.requireNonNull(tokenProvider, "tokenProvider");
        this.http = Objects.requireNonNull(http, "http");
        this.json = Objects.requireNonNull(mapper, "mapper");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.limiter = new SlidingWindowRateLimiter(unit, requestLimit, clock);
    }

    // ===== Public API =====
    public DocumentId createIntroduceGoods(Object document, String signature, String productGroup) {
        if (document == null) throw new IllegalArgumentException("document is null");
        if (signature == null || signature.isBlank()) throw new IllegalArgumentException("signature is blank");
        if (productGroup == null || productGroup.isBlank()) throw new IllegalArgumentException("productGroup is blank");

        try {
            limiter.acquire();

            String reqId = UUID.randomUUID().toString();
            long t0 = System.nanoTime();

            String productDocumentBase64 = base64EncodeJson(document);
            CreateDocumentRequest body = new CreateDocumentRequest(
                    "MANUAL", productDocumentBase64, "LP_INTRODUCE_GOODS", signature
            );

            URI uri = baseUri.resolve("/lk/documents/create?pg=" + encode(productGroup));
            String token = safeToken(tokenProvider.getToken());

            HttpRequest req = HttpRequest.newBuilder(uri)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(writeJson(body)))
                    .build();

            log.info("createIntroduceGoods start reqId={} pg={}", reqId, productGroup);

            HttpResponse<String> resp = sendOnce(req);
            if (is5xx(resp.statusCode())) {
                sleepQuiet(250);
                resp = sendOnce(req);
            }

            long tookMs = Duration.ofNanos(System.nanoTime() - t0).toMillis();

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                CreateDocumentResponse ok = readJson(resp.body(), CreateDocumentResponse.class);
                String id = (ok != null && ok.value != null) ? ok.value : "";
                if (id.isEmpty()) throw new TransportException("Empty id in 2xx response");
                log.info("createIntroduceGoods ok reqId={} pg={} id={} durationMs={}", reqId, productGroup, id, tookMs);
                return new DocumentId(id);
            }

            if (resp.statusCode() == 401 || resp.statusCode() == 403) {
                log.warn("auth failure {} reqId={} pg={} durationMs={}", resp.statusCode(), reqId, productGroup, tookMs);
                throw new AuthException(extractError(resp));
            }

            if (resp.statusCode() >= 400 && resp.statusCode() < 500) {
                log.warn("client error {} reqId={} pg={} durationMs={}", resp.statusCode(), reqId, productGroup, tookMs);
                throw new ApiException(extractError(resp));
            }

            log.error("server error {} reqId={} pg={} durationMs={}", resp.statusCode(), reqId, productGroup, tookMs);
            throw new TransportException("Server error " + resp.statusCode() + ": " + extractError(resp));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RateLimitInterruptedException("Interrupted while waiting in rate limiter");
        } catch (JsonProcessingException e) {
            throw new TransportException("JSON serialization error", e);
        } catch (IOException e) {
            throw new TransportException("I/O error", e);
        }
    }

    // ===== Internals =====
    private static ObjectMapper defaultMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private String base64EncodeJson(Object pojo) throws JsonProcessingException {
        String jsonStr = writeJson(pojo);
        byte[] bytes = jsonStr.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String writeJson(Object pojo) throws JsonProcessingException {
        return json.writeValueAsString(pojo);
    }

    private <T> T readJson(String body, Class<T> type) throws JsonProcessingException {
        return json.readValue(body, type);
    }

    private HttpResponse<String> sendOnce(HttpRequest req) throws IOException, InterruptedException {
        return http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static boolean is5xx(int code) { return code >= 500 && code < 600; }

    private String extractError(HttpResponse<String> resp) {
        String body = resp.body();
        if (body == null || body.isBlank()) return "HTTP " + resp.statusCode();
        try {
            ErrorResponse er = json.readValue(body, ErrorResponse.class);
            if (er != null && er.error_message != null && !er.error_message.isBlank()) return er.error_message;
        } catch (Exception ignored) { }
        return "HTTP " + resp.statusCode() + " body: " + trim(body, 512);
    }

    private static String trim(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private static String safeToken(String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("token is blank");
        return token;
    }

    private static void sleepQuiet(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    // ===== DTOs =====
    static final class CreateDocumentRequest {
        public final String document_format;
        public final String product_document; // base64(json)
        public final String type;             // "LP_INTRODUCE_GOODS"
        public final String signature;

        CreateDocumentRequest(String document_format, String product_document, String type, String signature) {
            this.document_format = document_format;
            this.product_document = product_document;
            this.type = type;
            this.signature = signature;
        }
    }

    static final class CreateDocumentResponse { public String value; }
    static final class ErrorResponse { public String error_message; }

    // Контракт лимитера: блокирует поток до разрешённого окна; может быть прерван (InterruptedException).
    // Позволяет подменять реализацию (DIP).
    interface RateLimiter { void acquire() throws InterruptedException; }

    static final class SlidingWindowRateLimiter implements RateLimiter {
        private final long windowNanos;
        private final int limit;
        private final Deque<Long> stamps = new ArrayDeque<>();
        private final Object monitor = new Object();
        private final Clock clock;

        SlidingWindowRateLimiter(TimeUnit unit, int limit, Clock clock) {
            this.windowNanos = unit.toNanos(1L);
            this.limit = limit;
            this.clock = clock;
        }

        @Override
        public void acquire() throws InterruptedException {
            long now = nanoNow();
            synchronized (monitor) {
                cleanup(now);
                while (stamps.size() >= limit) {
                    long oldest = stamps.peekFirst();
                    long waitNanos = (oldest + windowNanos) - now;
                    if (waitNanos <= 0) {
                        cleanup(nanoNow());
                        continue;
                    }
                    long waitMillis = Math.max(1L, waitNanos / 1_000_000L);
                    monitor.wait(waitMillis);
                    cleanup(nanoNow());
                    now = nanoNow();
                }
                stamps.addLast(nanoNow());
                monitor.notifyAll();
            }
        }

        private void cleanup(long now) {
            while (!stamps.isEmpty()) {
                long oldest = stamps.peekFirst();
                if (now - oldest >= windowNanos) stamps.removeFirst();
                else break;
            }
        }

        // Берём монотонное время для расчёта интервалов (не зависит от системных часов).
        // Нужно для корректной работы лимитера.
        private long nanoNow() {
            return System.nanoTime();
        }
    }
}