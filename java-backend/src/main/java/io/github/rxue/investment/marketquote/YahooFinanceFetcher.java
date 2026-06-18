package io.github.rxue.investment.marketquote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class YahooFinanceFetcher {

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
    public Price getCurrentPrice(String companySymbol) throws IOException {
        String crumb = getCrumb();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + companySymbol + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                .header("User-Agent", MOZILLA_5_0)
                .GET()
                .build();
        HttpResponse<String> response = send(request);
        return parseCurrentPrice(response.body());
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

    private Price parseCurrentPrice(String json) throws IOException {
        JsonNode result = objectMapper.readTree(json)
                .path("quoteResponse").path("result").get(0);
        String currency = result.path("currency").asText();
        BigDecimal price = result.path("regularMarketPrice").decimalValue();
        JsonNode regularMarketTime = result.path("regularMarketTime");
        ZonedDateTime timestamp = Instant.ofEpochSecond(regularMarketTime.asLong())
                .atZone(ZoneId.of(result.path("exchangeTimezoneName").asText()));
        return new Price(currency, price, timestamp);
    }
}
