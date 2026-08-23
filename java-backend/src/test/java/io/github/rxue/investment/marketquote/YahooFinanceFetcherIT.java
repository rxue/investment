package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Price;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static io.github.rxue.investment.marketquote.YahooFinanceFetcher.DIVIDEND_YIELD;
import static io.github.rxue.investment.marketquote.YahooFinanceFetcher.RETURN_ON_EQUITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class YahooFinanceFetcherIT {

    private final YahooFinanceFetcher fetcher = new YahooFinanceFetcher();

    @Test
    void getCurrentPrice_returnsCurrentPrice_for_real_symbol() {
        Price price = fetcher.getCurrentPrice("AAPL");

        assertNotNull(price);
        assertEquals("USD", price.currency());
        assertTrue(price.centValue() > 0,
                "price should be positive, was " + price.centValue());
        assertNotNull(price.timestamp());
        assertTrue(price.timestamp().isBefore(ZonedDateTime.now().plusMinutes(1)),
                "price timestamp should not be in the future, was " + price.timestamp());
    }

    @Test
    void getCurrentPrice_throws_exception_given_unknown_symbol() {
        assertThrows(IllegalArgumentException.class, () -> fetcher.getCurrentPrice("THIS_SYMBOL_DOES_NOT_EXIST_XYZ"));
    }

    @Test
    void getFundamentals_returns_metrics_for_real_symbol() {
        Map<String, Object> fundamentals = fetcher.getFundamentals("AAPL", List.of(
                "trailingPE", "dividendYield", RETURN_ON_EQUITY, YahooFinanceFetcher.REGULAR_MARKET_CHANGE_PERCENT));

        assertEquals(4, fundamentals.size());
        assertInstanceOf(Double.class, fundamentals.get("trailingPE"));
        assertInstanceOf(Double.class, fundamentals.get(RETURN_ON_EQUITY));
        assertInstanceOf(Double.class, fundamentals.get(YahooFinanceFetcher.REGULAR_MARKET_CHANGE_PERCENT));
        // AAPL may not pay a dividend, so dividendYield can legitimately be null; only check its type when present.
        Object dividendYield = fundamentals.get(DIVIDEND_YIELD);
        if (dividendYield != null) {
            assertInstanceOf(Double.class, dividendYield);
        }
    }

    @Test
    void getFundamentals_throws_exception_given_unknown_symbol() {
        assertThrows(IllegalArgumentException.class,
                () -> fetcher.getFundamentals("THIS_SYMBOL_DOES_NOT_EXIST_XYZ", List.of("trailingPE")));
    }

}
