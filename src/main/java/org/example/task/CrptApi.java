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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CrptApi {

    // ===== Public API =====
    public interface TokenProvider { String getToken(); }

    public static final class DocumentId {
        public final String value;
        public DocumentId(String value) { this.value = Objects.requireNonNull(value, "value"); }
        @Override public String toString() { return value; }
    }

    // Минимальная модель LP_INTRODUCE_GOODS (дополняй по необходимости)
    public static class Document {
        public String participant_inn;
        public String producer_inn;
        public String production_date;   // YYYY-MM-DD
        public String production_type;   // OWN_PRODUCTION | CONTRACT_PRODUCTION
        public java.util.List<Product> products;

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
        }
    }

    // ===== Exceptions =====
    public static class ApiException extends RuntimeException { public ApiException(String m) { super(m); } }
    public static class AuthException extends RuntimeException { public AuthException(String m) { super(m); } }
    public static class TransportException extends RuntimeException {
        public TransportException(String m, Throwable c) { super(m, c); }
        public TransportException(String m) { super(m); }
    }
    public static class RateLimitInterruptedException extends RuntimeException { public RateLimitInterruptedException(String m) { super(m); } }

    // ===== State =====
    private static final Logger LOG = Logger.getLogger(CrptApi.class.getName());

    private final URI baseUri;
    private final HttpClient http;
    private final ObjectMapper json;
    private final TokenProvider tokenProvider;
    private final RateLimiter limiter;
    private final Clock clock;

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

            logInfo("createIntroduceGoods start", reqId, productGroup, null, 0);

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
                logInfo("createIntroduceGoods ok", reqId, productGroup, id, tookMs);
                return new DocumentId(id);
            }

            if (resp.statusCode() == 401 || resp.statusCode() == 403) {
                logWarn("auth failure " + resp.statusCode(), reqId, productGroup, null, tookMs);
                throw new AuthException(extractError(resp));
            }

            if (resp.statusCode() >= 400 && resp.statusCode() < 500) {
                logWarn("client error " + resp.statusCode(), reqId, productGroup, null, tookMs);
                throw new ApiException(extractError(resp));
            }

            logError("server error " + resp.statusCode(), reqId, productGroup, null, tookMs, null);
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

    // ===== Rate limiter =====
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
                if (now - oldest >= windowNanos) {
                    stamps.removeFirst();
                } else break;
            }
        }

        private long nanoNow() {
            // От System.nanoTime() берём монотонность; Clock пригодится для тестов/расширений.
            return System.nanoTime();
        }
    }

    // ===== Logging helpers =====
    private static void logInfo(String msg, String reqId, String pg, String docId, long tookMs) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, () -> String.format("%s reqId=%s pg=%s id=%s durationMs=%d",
                    msg, reqId, safe(pg), safe(docId), tookMs));
        }
    }

    private static void logWarn(String msg, String reqId, String pg, String docId, long tookMs) {
        if (LOG.isLoggable(Level.WARNING)) {
            LOG.log(Level.WARNING, () -> String.format("%s reqId=%s pg=%s id=%s durationMs=%d",
                    msg, reqId, safe(pg), safe(docId), tookMs));
        }
    }

    private static void logError(String msg, String reqId, String pg, String docId, long tookMs, Throwable t) {
        LOG.log(Level.SEVERE, String.format("%s reqId=%s pg=%s id=%s durationMs=%d",
                msg, reqId, safe(pg), safe(docId), tookMs), t);
    }

    private static String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }
}