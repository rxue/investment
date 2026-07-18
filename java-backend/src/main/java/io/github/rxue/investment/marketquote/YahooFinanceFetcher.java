package io.github.rxue.investment.marketquote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class YahooFinanceFetcher {
    private static final String ENDPOINT_ROOT_URL = "https://query1.finance.yahoo.com/v8/finance";
    private static final String CRUMB_URL = "https://query2.finance.yahoo.com/v1/test/getcrumb";
    private static final String MOZILLA_5_0 = "Mozilla/5.0";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public YahooFinanceFetcher() {
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.objectMapper = new ObjectMapper();
    }
    public Price getCurrentPrice(String companySymbol) {
        JsonNode resultNode;
        try {
            String crumb = getCrumb();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT_ROOT_URL + "/chart/" + companySymbol
                            + "?interval=1d"
                            + "&range=1d"
                            + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                    .header("User-Agent", MOZILLA_5_0)
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);
            resultNode = objectMapper.readTree(response.body())
                    .path("chart").path("result").get(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (resultNode == null) {
            throw new IllegalArgumentException("Cannot fetch any price with the given company symbol " + companySymbol);
        }
        return parseCurrentPrice(resultNode);
    }

    public Price getClosePrice(String symbol, LocalDate date) {
        JsonNode resultNode;
        try {
            String crumb = getCrumb();
            long period1 = date.minusDays(7).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            long period2 = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT_ROOT_URL + "/chart/" + symbol
                            + "?period1=" + period1
                            + "&period2=" + period2
                            + "&interval=1d"
                            + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                    .header("User-Agent", MOZILLA_5_0)
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);
            resultNode = objectMapper.readTree(response.body())
                    .path("chart").path("result").get(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (resultNode == null) {
            throw new IllegalArgumentException("Cannot fetch any price with the given company symbol " + symbol + " on or before " + date);
        }
        return parseHistoricalPrice(resultNode, symbol, date);
    }

    private String getCrumb() throws IOException {
        send(HttpRequest.newBuilder()
                .uri(URI.create("https://fc.yahoo.com/"))
                .header("User-Agent", MOZILLA_5_0)
                .GET()
                .build());
        HttpResponse<String> crumbResponse = send(HttpRequest.newBuilder()
                .uri(URI.create(CRUMB_URL))
                .header("User-Agent", MOZILLA_5_0)
                .GET()
                .build());
        return crumbResponse.body();
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request interrupted", e);
        }
    }

    private Price parseCurrentPrice(JsonNode result) {
        JsonNode meta = result.path("meta");
        String currency = meta.path("currency").asText();
        BigDecimal price = meta.path("regularMarketPrice").decimalValue();
        JsonNode regularMarketTime = meta.path("regularMarketTime");
        ZonedDateTime timestamp = Instant.ofEpochSecond(regularMarketTime.asLong())
                .atZone(ZoneId.of(meta.path("exchangeTimezoneName").asText()));
        return new Price(currency, price, timestamp);
    }

    private Price parseHistoricalPrice(JsonNode result, String symbol, LocalDate date) {
        String currency = result.path("meta").path("currency").asText();
        ZoneId exchangeZone = ZoneId.of(result.path("meta").path("exchangeTimezoneName").asText());
        JsonNode timestamps = result.path("timestamp");
        JsonNode closes = result.path("indicators").path("quote").get(0).path("close");

        int latestIndex = -1;
        for (int i = 0; i < timestamps.size(); i++) {
            LocalDate candidateDate = Instant.ofEpochSecond(timestamps.get(i).asLong()).atZone(exchangeZone).toLocalDate();
            if (!candidateDate.isAfter(date) && !closes.get(i).isNull()) {
                latestIndex = i;
            }
        }
        if (latestIndex < 0) {
            throw new IllegalArgumentException("No historical price found for company symbol " + symbol + " on or before " + date);
        }
        BigDecimal price = closes.get(latestIndex).decimalValue();
        ZonedDateTime timestamp = Instant.ofEpochSecond(timestamps.get(latestIndex).asLong()).atZone(exchangeZone);
        return new Price(currency, price, timestamp);
    }
}
