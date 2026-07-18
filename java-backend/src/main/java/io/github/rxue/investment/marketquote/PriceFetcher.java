package io.github.rxue.investment.marketquote;

import java.io.IOException;
import java.io.UncheckedIOException;
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

    public Price getCurrentPriceInEuro(String companySymbol) {
        Price price;
        try {
            price = priceFetcher.getCurrentPrice(companySymbol);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to fetch current price for " + companySymbol, e);
        }
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, LocalDate.now());
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }
}
