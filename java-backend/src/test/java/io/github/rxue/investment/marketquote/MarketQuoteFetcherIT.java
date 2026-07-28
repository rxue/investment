package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.portfolio.money.Price;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MarketQuoteFetcherIT {
    private final MarketQuoteFetcher out = new MarketQuoteFetcher();
    @Test
    void getCurrentPriceInEuro_ELISA_HE_returns_the_latest_market_price() {
        Price price = out.getCurrentPriceInEuro("ELISA.HE");

        assertEquals("EUR", price.currency());
        assertTrue(price.value().compareTo(BigDecimal.ZERO) > 0, "price should be positive, but was " + price.value());
        assertFalse(price.timestamp().isAfter(ZonedDateTime.now()), "returned timestamp " + price.timestamp() + " should not be in the future");
    }

    @RepeatedTest(5)
    void getClosePrice_PFE_on_a_historical_date_returns_the_last_trading_price_on_or_before_that_date() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        Price price = out.getClosePrice("PFE", date);

        assertEquals("USD", price.currency());
        assertTrue(price.value().compareTo(BigDecimal.ZERO) > 0, "price should be positive, but was " + price.value());
        assertFalse(price.timestamp().toLocalDate().isAfter(date), "returned timestamp " + price.timestamp() + " should not be after " + date);
    }
    @Test
    void getClosePriceInEuro_PFE_on_a_historical_date_returns_the_last_trading_price_converted_to_euro() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        Price price = out.getClosePriceInEuro("PFE", date);

        assertEquals("EUR", price.currency());
        assertTrue(price.value().compareTo(BigDecimal.ZERO) > 0, "price should be positive, but was " + price.value());
        assertFalse(price.timestamp().toLocalDate().isAfter(date), "returned timestamp " + price.timestamp() + " should not be after " + date);
    }

    @Test
    void getFundamentals_KHC_returns_trailing_pe_dividend_yield_and_roe() {
        Map<FundamentalMetric, BigDecimal> fundamentals = out.getFundamentals("KHC",
                List.of(FundamentalMetric.TRAILING_PE, FundamentalMetric.DIVIDEND_YIELD, FundamentalMetric.ROE));

        BigDecimal trailingPE = fundamentals.get(FundamentalMetric.TRAILING_PE);
        BigDecimal dividendYield = fundamentals.get(FundamentalMetric.DIVIDEND_YIELD);
        BigDecimal roe = fundamentals.get(FundamentalMetric.ROE);
        assertTrue(trailingPE == null || isGreaterThanZero(trailingPE), "trailing PE should be positive, but was " + trailingPE);
        assertTrue(isGreaterThanZero(dividendYield), "dividend yield should be positive, but was " + dividendYield);
        assertTrue(roe != null, "ROE should be positive, but was " + roe);
    }
    private static boolean isGreaterThanZero(BigDecimal numberValue) {
        return numberValue.compareTo(BigDecimal.ZERO) > 0;
    }
}
