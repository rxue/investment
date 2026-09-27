package io.github.rxue.investment.adapter.marketquote.yahoofinance;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.FormattedNumber;

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

    private static FormattedNumber toNumber(JsonNode numberNode) {
        JsonNode raw = numberNode.path("raw");
        return raw.isNumber() ? new FormattedNumber(raw.decimalValue(), numberNode.path("fmt").textValue()) : null;
    }
    static YahooMetric of(QuoteMetric quoteMetric) {
        return switch(quoteMetric) {
            case REGULAR_MARKET_PRICE -> REGULAR_MARKET_PRICE;
            case CURRENCY -> CURRENCY;
            case REGULAR_MARKET_TIME -> REGULAR_MARKET_TIME;
            case REGULAR_MARKET_CHANGE -> REGULAR_MARKET_CHANGE;
            case REGULAR_MARKET_CHANGE_PERCENT -> REGULAR_MARKET_CHANGE_PERCENT;
            case DIVIDEND_YIELD -> DIVIDEND_YIELD;
        };
    }
}