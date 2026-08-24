package io.github.rxue.investment.marketquote;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.github.rxue.investment.marketquote.YahooFinanceFetcher.RETURN_ON_EQUITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class YahooFinanceFetcherIT {

    private final YahooFinanceFetcher fetcher = new YahooFinanceFetcher();

    @Test
    void getCurrentPrice_returnsCurrentPrice_for_real_symbol() {
        RawPrice price = fetcher.getCurrentPrice("AAPL");

        assertNotNull(price);
        assertEquals("USD", price.currency());
        assertTrue(price.value() > 0,
                "price should be positive, was " + price.value());
        assertTrue(price.timestamp() <= Instant.now().plusSeconds(60).getEpochSecond(),
                "price timestamp should not be in the future, was " + price.timestamp());
    }

    @Test
    void getCurrentPrice_throws_exception_given_unknown_symbol() {
        assertThrows(IllegalArgumentException.class, () -> fetcher.getCurrentPrice("THIS_SYMBOL_DOES_NOT_EXIST_XYZ"));
    }

    @Test
    void getFundamentalMetrics_returns_metrics_for_real_symbol() {
        Map<String, Double> fundamentals = fetcher.getFundamentalMetrics("AAPL", List.of(
                "trailingPE", "dividendYield", RETURN_ON_EQUITY, YahooFinanceFetcher.REGULAR_MARKET_CHANGE_PERCENT));

        assertEquals(4, fundamentals.size());
        assertNotNull(fundamentals.get("trailingPE"));
        assertNotNull(fundamentals.get(RETURN_ON_EQUITY));
        assertNotNull(fundamentals.get(YahooFinanceFetcher.REGULAR_MARKET_CHANGE_PERCENT));
        // AAPL may not pay a dividend, so dividendYield can legitimately be null; no assertion needed either way.
    }

    @Test
    void getFundamentalMetrics_throws_exception_given_unknown_symbol() {
        assertThrows(IllegalArgumentException.class,
                () -> fetcher.getFundamentalMetrics("THIS_SYMBOL_DOES_NOT_EXIST_XYZ", List.of("trailingPE")));
    }

}
