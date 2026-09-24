package io.github.rxue.investment.marketquote.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rxue.investment.marketquote.Metric;

import java.util.function.Function;

public enum YahooMetric implements Metric {
    REGULAR_MARKET_PRICE("price", "Regular Market Price", jsonNode -> toNumber(jsonNode.path("regularMarketPrice"))),
    CURRENCY("price", "Currency", jsonNode -> jsonNode.path("currency").textValue()),
    REGULAR_MARKET_TIME("price", "Regular Market Time", jsonNode -> jsonNode.path("regularMarketTime").longValue()),
    REGULAR_MARKET_CHANGE("price", "Regular Market Change", jsonNode -> toNumber(jsonNode.path("regularMarketChange"))),
    REGULAR_MARKET_CHANGE_PERCENT("price", "Regular Market Change Percent", jsonNode -> toNumber(jsonNode.path("regularMarketChangePercent"))),
    GMT_OFFSET_IN_MILLISECONDS("quoteType", "GMT Offset in Milliseconds", jsonNode -> jsonNode.path("gmtOffSetMilliseconds").longValue()),
    DIVIDEND_YIELD("summaryDetail", "Dividend Yield", jsonNode -> toNumber(jsonNode.path("dividendYield")));

    private final String v10Module;
    private final String descriptiveName;
    private final Function<JsonNode,Comparable<?>> parser;
    YahooMetric(String v10Module, String descriptiveName, Function<JsonNode,Comparable<?>> parser) {
        this.v10Module = v10Module;
        this.descriptiveName = descriptiveName;
        this.parser = parser;
    }

    public String v10Module() {
        return v10Module;
    }

    public Function<JsonNode, Comparable<?>> parser() {
        return parser;
    }

    private static YahooNumber toNumber(JsonNode numberNode) {
        return new YahooNumber(numberNode.path("raw").decimalValue(), numberNode.path("fmt").textValue());
    }
}
