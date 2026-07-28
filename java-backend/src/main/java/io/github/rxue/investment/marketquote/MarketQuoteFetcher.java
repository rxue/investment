package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.portfolio.money.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketQuoteFetcher {
    private static final String EUR = "EUR";

    private static final Map<FundamentalMetric, String> YAHOO_FIELD_BY_METRIC = Map.of(
            FundamentalMetric.TRAILING_PE, "trailingPE",
            FundamentalMetric.DIVIDEND_YIELD, "dividendYield",
            FundamentalMetric.ROE, "returnOnEquity"
    );

    private final YahooFinanceFetcher yahooFinanceFetcher;
    private final FXRateFetcher fxRateFetcher;
    private MarketQuoteFetcher(YahooFinanceFetcher yahooFinanceFetcher, FXRateFetcher fxRateFetcher) {
        this.yahooFinanceFetcher = yahooFinanceFetcher;
        this.fxRateFetcher = fxRateFetcher;
    }
    public MarketQuoteFetcher() {
        this(new YahooFinanceFetcher(), new FXRateFetcher());
    }

    public Price getCurrentPrice(String symbol) {
        return yahooFinanceFetcher.getCurrentPrice(symbol);
    }

    public Price getCurrentPriceInEuro(String symbol) {
        Price price = getCurrentPrice(symbol);
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, LocalDate.now());
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }
    public Price getClosePrice(String symbol, LocalDate date) {
        return yahooFinanceFetcher.getClosePrice(symbol, date);
    }
    public Price getClosePriceInEuro(String symbol, LocalDate date) {
        Price price = yahooFinanceFetcher.getClosePrice(symbol, date);
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, date);
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }

    public Map<FundamentalMetric, BigDecimal> getFundamentals(String symbol, List<FundamentalMetric> fundamentalMetrics) {
        List<String> yahooFieldNames = fundamentalMetrics.stream()
                .map(YAHOO_FIELD_BY_METRIC::get)
                .collect(Collectors.toList());
        Map<String, BigDecimal> valuesByYahooField = yahooFinanceFetcher.getFundamentals(symbol, yahooFieldNames);
        Map<FundamentalMetric, BigDecimal> values = new EnumMap<>(FundamentalMetric.class);
        for (FundamentalMetric metric : fundamentalMetrics) {
            values.put(metric, valuesByYahooField.get(YAHOO_FIELD_BY_METRIC.get(metric)));
        }
        return values;
    }
}
