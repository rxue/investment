package io.github.rxue.investment.marketquote.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rxue.investment.marketquote.Metric;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class YahooMetric<T> implements Metric {
    private static final Map<String, YahooMetric<?>> ALL = new HashMap<>();
    public static final YahooMetric<BigDecimal> REGULAR_MARKET_PRICE = of("REGULAR_MARKET_PRICE", "price", "Regular Market Price", jsonNode -> jsonNode.path("regularMarketPrice").path("raw").decimalValue());
    public static final YahooMetric<Long> REGULAR_MARKET_TIME = of("REGULAR_MARKET_TIME", "price", "Regular Market Time", jsonNode -> jsonNode.path("regularMarketTime").longValue());
    public static final YahooMetric<String> CURRENCY = of("CURRENCY", "price", "Currency", jsonNode -> jsonNode.path("currency").textValue());
    public static final YahooMetric<Long> GMT_OFFSET_IN_MILLISECONDS = of("GMT_OFFSET_MILLISECONDS", "quoteType", "GMT Offset in Milliseconds", jsonNode -> jsonNode.path("gmtOffSetMilliseconds").longValue());
    public static final YahooMetric<YahooNumber> REGULAR_MARKET_CHANGE_PERCENT = of("REGULAR_MARKET_CHANGE_PERCENT", "price", "Regular Market Change Percent", jsonNode -> toNumber(jsonNode.path("regularMarketChangePercent")));
    private final String name;
    private final String v10Module;
    private final String descriptiveName;
    private final Function<JsonNode, T> parser;

    private YahooMetric(String name, String v10Module, String descriptiveName, Function<JsonNode, T> parser) {
        this.name = name;
        this.v10Module = v10Module;
        this.descriptiveName = descriptiveName;
        this.parser = parser;
    }

    public String v10Module() {
        return v10Module;
    }
    @Override
    public String name() {
        return descriptiveName;
    }

    public Function<JsonNode, T> parser() {
        return parser;
    }

    private static <T> YahooMetric<T> of(String name, String v10Module, String descriptiveName, Function<JsonNode, T> parser) {
        YahooMetric<T> newMetric = new YahooMetric<>(name, v10Module, descriptiveName, parser);
        ALL.put(name, newMetric);
        return newMetric;
    }
    private static YahooNumber toNumber(JsonNode numberNode) {
        return new YahooNumber(numberNode.path("raw").decimalValue(), numberNode.path("fmt").textValue());
    }

    public static YahooMetric<?> get(String metricName) {
        return ALL.get(metricName);
    }

}
