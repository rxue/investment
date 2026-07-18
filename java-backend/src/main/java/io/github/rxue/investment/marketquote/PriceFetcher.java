package io.github.rxue.investment.marketquote;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceFetcher {
    private static final String EUR = "EUR";

    private final YahooFinanceFetcher priceFetcher;
    private final FXRateFetcher fxRateFetcher;
    private PriceFetcher(YahooFinanceFetcher priceFetcher, FXRateFetcher fxRateFetcher) {
        this.priceFetcher = priceFetcher;
        this.fxRateFetcher = fxRateFetcher;
    }
    public PriceFetcher() {
        this(new YahooFinanceFetcher(), new FXRateFetcher());
    }

    public Price getCurrentPriceInEuro(String symbol) {
        Price price = priceFetcher.getCurrentPrice(symbol);
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, LocalDate.now());
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }

    public Price getClosePrice(String symbol, LocalDate date) {
        return priceFetcher.getClosePrice(symbol, date);
    }
    public Price getClosePriceInEuro(String symbol, LocalDate date) {
        Price price = priceFetcher.getClosePrice(symbol, date);
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, date);
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }
}
