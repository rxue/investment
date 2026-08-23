package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Price;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YahooFinanceFetcherIT {

    private final YahooFinanceFetcher fetcher = new YahooFinanceFetcher();

    @Test
    void getCurrentPrice_returnsCurrentPriceForRealSymbol() {
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
    void getCurrentPrice_unknownSymbolThrows() {
        assertThrows(RuntimeException.class, () -> fetcher.getCurrentPrice("THIS_SYMBOL_DOES_NOT_EXIST_XYZ"));
    }

    
}
