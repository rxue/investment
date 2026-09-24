package io.github.rxue.investment.marketquote.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.*;

public class QuoteSummaryFetcher {
    private static final String MOZILLA_5_0 = "Mozilla/5.0";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public QuoteSummaryFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get values of the given Yahoo Metrics from modules of quoteSummary endpoint
     *
     * @param yahooTickerSymbol
     * @param yahooMetrics
     * @return
     */
    public YahooMetricValues getValues(String yahooTickerSymbol, Collection<YahooMetric> yahooMetrics) {
        Metrics metrics = new Metrics(yahooMetrics);
        JsonNode fullQuotesNode = getFullQuotesNode(yahooTickerSymbol, metrics.modules());
        Map<String,List<YahooMetric>> metricByModule = metrics.groupByModule();
        List<Map<YahooMetric,Comparable<?>>> result = new ArrayList<>();
        metricByModule.forEach((module, yahooMetricList) -> {
            JsonNode moduleNode = fullQuotesNode.path(module);
            result.add(parseModule(moduleNode, yahooMetricList));
        });
        Map<YahooMetric,Comparable<?>> resultMap =  result.stream()
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new YahooMetricValues(resultMap);
    }

    private JsonNode getFullQuotesNode(String yahooTickerSymbol, String commaDelimitedModules) {
        JsonNode resultNode;
        try {
            String crumb = getCrumb();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://query1.finance.yahoo.com/v10/finance/quoteSummary" + "/" + yahooTickerSymbol + "?"
                            + "modules=" + commaDelimitedModules + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                    .header("User-Agent", MOZILLA_5_0)
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);
            resultNode = objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return resultNode.path("quoteSummary")
                .path("result")
                .get(0);
    }

    private static Map<YahooMetric,Comparable<?>> parseModule(JsonNode moduleNode, List<YahooMetric> metrics) {
        Map<YahooMetric,Comparable<?>> result = new HashMap<>();
        for (YahooMetric metric : metrics) {
            result.put(metric, metric.parser().apply(moduleNode));
        }
        return Collections.unmodifiableMap(result);
    }

    private String getCrumb() throws IOException {
        send(HttpRequest.newBuilder()
                .uri(URI.create("https://fc.yahoo.com/"))
                .header("User-Agent", MOZILLA_5_0)
                .GET()
                .build());
        HttpResponse<String> crumbResponse = send(HttpRequest.newBuilder()
                .uri(URI.create("https://query2.finance.yahoo.com/v1/test/getcrumb"))
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
    private record Metrics(Collection<YahooMetric> values) {
        Map<String,List<YahooMetric>> groupByModule() {
            return values.stream()
                    .collect(groupingBy(YahooMetric::v10Module));
        }
        /**
         *
         * @return modules separated by comma
         */
        String modules() {
            return values.stream()
                    .map(YahooMetric::v10Module)
                    .distinct()
                    .collect(joining(","));
        }
    }
}
