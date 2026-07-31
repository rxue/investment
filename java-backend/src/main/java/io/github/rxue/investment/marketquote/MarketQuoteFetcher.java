package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MarketQuoteFetcher {
    private static final String EUR = "EUR";

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

    public Map<String, BigDecimal> getFundamentals(String symbol, List<String> yahooFieldNames) {
        return yahooFinanceFetcher.getFundamentals(symbol, yahooFieldNames);
    }
}
