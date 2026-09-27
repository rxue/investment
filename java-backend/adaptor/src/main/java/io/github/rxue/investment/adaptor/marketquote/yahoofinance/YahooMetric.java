package io.github.rxue.investment.adaptor.marketquote.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

public enum YahooMetric {
    REGULAR_MARKET_PRICE("price", jsonNode -> toNumber(jsonNode.path("regularMarketPrice"))),
    CURRENCY("price", jsonNode -> jsonNode.path("currency").textValue()),
    REGULAR_MARKET_TIME("price", jsonNode -> jsonNode.path("regularMarketTime").longValue()),
    REGULAR_MARKET_CHANGE("price", jsonNode -> toNumber(jsonNode.path("regularMarketChange"))),
    REGULAR_MARKET_CHANGE_PERCENT("price", jsonNode -> toNumber(jsonNode.path("regularMarketChangePercent"))),
    GMT_OFFSET_IN_MILLISECONDS("quoteType", jsonNode -> jsonNode.path("gmtOffSetMilliseconds").longValue()),
    DIVIDEND_YIELD("summaryDetail", jsonNode -> toNumber(jsonNode.path("dividendYield")));

    private final String v10Module;
    private final Function<JsonNode,Comparable<?>> parser;
    YahooMetric(String v10Module, Function<JsonNode,Comparable<?>> parser) {
        this.v10Module = v10Module;
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
