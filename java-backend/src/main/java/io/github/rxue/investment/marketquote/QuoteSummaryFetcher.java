package io.github.rxue.investment.marketquote;

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
import java.util.function.BiFunction;

import static java.util.stream.Collectors.groupingBy;

public class QuoteSummaryFetcher {
    private static final String MOZILLA_5_0 = "Mozilla/5.0";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public QuoteSummaryFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }
    private static String getModule(List<YahooMetric> metrics) {
        return metrics.stream().findFirst()
                .get()
                .getModule();
    }

    public Map<YahooMetric,Object> getMetrics(String yahooTickerSymbol, Collection<YahooMetric> yahooMetrics) {
        Map<String,List<YahooMetric>> metricsByModule = yahooMetrics.stream()
                .collect(groupingBy(YahooMetric::getModule));
        Map<YahooMetric,Object> result = new HashMap<>();
        metricsByModule.values()
                .forEach(metrics -> result.putAll(getModuleQuotes(yahooTickerSymbol, metrics)));
        return Collections.unmodifiableMap(result);
    }

    private Map<YahooMetric,Object> getModuleQuotes(String yahooTickerSymbol, List<YahooMetric> metrics) {
        JsonNode resultNode;
        try {
            String crumb = getCrumb();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://query1.finance.yahoo.com/v10/finance/quoteSummary" + "/" + yahooTickerSymbol + "?"
                            + "modules=" + getModule(metrics) + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8)))
                    .header("User-Agent", MOZILLA_5_0)
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);
            resultNode = objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return parse(resultNode, metrics);
    }

    private static Map<YahooMetric,Object> parse(JsonNode jsonNode, List<YahooMetric> metrics) {
        BiFunction<JsonNode,String,Object> getFieldValue= (moduleNode,attribute) -> {
            JsonNode attributeNode = moduleNode.path(attribute);
            if (!attributeNode.isValueNode()) {
                JsonNode subNode = attributeNode.path("raw");
                if (subNode.isNumber()) {
                    return subNode.decimalValue();
                }
                return subNode.asText();
            } else if (attributeNode.isInt()) {
                return attributeNode.longValue();
            }
            return attributeNode.textValue();
        };
        final String module = metrics.stream()
                .findFirst()
                .get()
                .getModule();
        final JsonNode priceNode = jsonNode.path("quoteSummary")
                .path("result")
                .get(0).path(module);
        Map<YahooMetric,Object> result = new HashMap<>();
        for (YahooMetric metric : metrics) {
            result.put(metric, getFieldValue.apply(priceNode, metric.getName()));
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

}
