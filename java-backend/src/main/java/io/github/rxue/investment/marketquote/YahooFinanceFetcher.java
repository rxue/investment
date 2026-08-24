package io.github.rxue.investment.marketquote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import java.util.*;
import java.util.stream.Collectors;

class YahooFinanceFetcher {
    private static final String ENDPOINT_ROOT_URL = "https://query1.finance.yahoo.com/v8/finance";
    private static final String QUOTE_SUMMARY_URL = "https://query1.finance.yahoo.com/v10/finance/quoteSummary";
    private static final String CRUMB_URL = "https://query2.finance.yahoo.com/v1/test/getcrumb";
    private static final String MOZILLA_5_0 = "Mozilla/5.0";

    static final String REGULAR_MARKET_CHANGE_PERCENT = "regularMarketChangePercent";
    static final String RETURN_ON_EQUITY = "returnOnEquity";
    public static final String DIVIDEND_YIELD = "dividendYield";
    private static final Map<String, String> FUNDAMENTAL_METRIC_TO_SECTION = Map.of(
            "trailingPE", "summaryDetail",
            DIVIDEND_YIELD, "summaryDetail",
            RETURN_ON_EQUITY, "financialData",
            REGULAR_MARKET_CHANGE_PERCENT, "price"
    );

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public YahooFinanceFetcher() {
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.objectMapper = new ObjectMapper();
    }
    public RawPrice getCurrentPrice(String companySymbol) {
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

    /**
     *
     * @param symbol
     * @param metricNames
     * @return map from metric name to its raw value, or null if the metric is not present in the response
     */
    public Map<String, Double> getFundamentalMetrics(String symbol, Collection<String> metricNames) {
        if (metricNames.isEmpty()) {
            return Map.of();
        }
        Set<String> modules = metricNames.stream()
                .map(FUNDAMENTAL_METRIC_TO_SECTION::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        JsonNode resultNode;
        try {
            String crumb = getCrumb();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(QUOTE_SUMMARY_URL + "/" + symbol
                            + "?modules=" + URLEncoder.encode(String.join(",", modules), StandardCharsets.UTF_8)
                            + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                    .header("User-Agent", MOZILLA_5_0)
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);
            resultNode = objectMapper.readTree(response.body())
                    .path("quoteSummary").path("result").get(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (resultNode == null) {
            throw new IllegalArgumentException("Cannot fetch fundamentals with the given company symbol " + symbol);
        }
        return parseFundamentals(resultNode, metricNames);
    }

    public RawPrice getClosePrice(String symbol, LocalDate date) {
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

    private RawPrice parseCurrentPrice(JsonNode result) {
        JsonNode meta = result.path("meta");
        String currency = meta.path("currency").asText();
        double price = meta.path("regularMarketPrice").doubleValue();
        long timestamp = meta.path("regularMarketTime").asLong();
        return new RawPrice(price, currency, timestamp);
    }

    private Map<String, Double> parseFundamentals(JsonNode result, Collection<String> metricNames) {

        Map<String, Double> values = new LinkedHashMap<>();
        for (String metricName : metricNames) {
            JsonNode valueNode = result.path(FUNDAMENTAL_METRIC_TO_SECTION.get(metricName))
                    .path(metricName)
                    .path("raw");
            if (!valueNode.isNumber()) {
                values.put(metricName, null);
                continue;
            }
            double value = valueNode.doubleValue();
            values.put(metricName, value);
        }
        return values;
    }

    private RawPrice parseHistoricalPrice(JsonNode result, String symbol, LocalDate date) {
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
        double price = closes.get(latestIndex).doubleValue();
        long timestamp = timestamps.get(latestIndex).asLong();
        return new RawPrice(price, currency, timestamp);
    }
}
