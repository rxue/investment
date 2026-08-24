package io.github.rxue.investment.marketquote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public class FxRateFetcher {
    public class FXRateFetcher {

        private static final String EXR_DATA_URL = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A";

        private final HttpClient httpClient;
        private final ObjectMapper objectMapper;

        public FXRateFetcher() {
            this.httpClient = HttpClient.newBuilder()
                    .cookieHandler(new CookieManager())
                    .build();
            this.objectMapper = new ObjectMapper();
        }

        public Map.Entry<String, BigDecimal> getFXRateFromEuro(String currency, LocalDate date) {
            if ("EUR".equals(currency)) {
                return new AbstractMap.SimpleImmutableEntry<>(date.format(DateTimeFormatter.ISO_LOCAL_DATE), BigDecimal.ONE);
            }
            try {
                HttpResponse<String> response = send(buildRequest(currency.trim().toUpperCase(), date));
                if (response.statusCode() == 404) {
                    throw new IllegalStateException("No ECB exchange rate found for " + currency + " up to " + date);
                }
                return parseLatestRate(response.body());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to fetch ECB exchange rate for " + currency, e);
            }
        }

        private HttpRequest buildRequest(String currency, LocalDate date) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String url = String.format(EXR_DATA_URL, currency)
                    + "?startPeriod=" + date.minusDays(7).format(formatter)
                    + "&endPeriod=" + date.format(formatter)
                    + "&format=jsondata";
            return HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
        }

        private HttpResponse<String> send(HttpRequest request) throws IOException {
            try {
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("HTTP request interrupted", e);
            }
        }

        private Map.Entry<String, BigDecimal> parseLatestRate(String json) throws IOException {
            JsonNode root = objectMapper.readTree(json);
            JsonNode series = root.path("dataSets").get(0).path("series");
            Iterator<String> seriesKeys = series.fieldNames();
            if (!seriesKeys.hasNext()) {
                throw new IllegalStateException("No exchange rate series found in ECB response");
            }
            JsonNode observations = series.path(seriesKeys.next()).path("observations");
            int latestIndex = -1;
            for (Iterator<String> it = observations.fieldNames(); it.hasNext(); ) {
                latestIndex = Math.max(latestIndex, Integer.parseInt(it.next()));
            }
            if (latestIndex < 0) {
                throw new IllegalStateException("No exchange rate observations found in ECB response");
            }
            BigDecimal value = observations.path(String.valueOf(latestIndex)).get(0).decimalValue();
            String observationDateText = resolveObservationDateText(root, latestIndex);
            return new AbstractMap.SimpleImmutableEntry<>(observationDateText, value);
        }

        private String resolveObservationDateText(JsonNode root, int observationIndex) {
            for (JsonNode dimension : root.path("structure").path("dimensions").path("observation")) {
                if ("TIME_PERIOD".equals(dimension.path("id").asText())) {
                    JsonNode timePeriodValue = dimension.path("values").get(observationIndex);
                    if (timePeriodValue == null) {
                        throw new IllegalStateException("No TIME_PERIOD value found for observation index " + observationIndex);
                    }
                    return timePeriodValue.path("id").asText();
                }
            }
            throw new IllegalStateException("No TIME_PERIOD dimension found in ECB response");
        }
    }
}
