package io.github.rxue.investment.marketquote;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EuroPriceFetcher {
    private static final String EUR = "EUR";

    private final YahooFinanceFetcher priceFetcher;
    private final FXRateFetcher fxRateFetcher;
    public EuroPriceFetcher(YahooFinanceFetcher priceFetcher, FXRateFetcher fxRateFetcher) {
        this.priceFetcher = priceFetcher;
        this.fxRateFetcher = fxRateFetcher;
    }

    public Price getCurrentEuroPrice(String companySymbol) throws IOException {
        Price price = priceFetcher.getCurrentPrice(companySymbol);
        String currency = price.currency();
        if (EUR.equals(currency)) {
            return price;
        }
        BigDecimal fxRate = fxRateFetcher.fetchFXRateToEuro(currency, LocalDate.now());
        BigDecimal priceInEuro = price.value().multiply(fxRate);
        return new Price(EUR, priceInEuro, price.timestamp());
    }
}
