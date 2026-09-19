package io.github.rxue.investment.marketquote;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.github.rxue.investment.marketquote.YahooMetric.*;

class QuoteSummaryFetcherIT {
    @Test
    void getPriceQuote_with_all_cases() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        QuoteSummaryFetcher fetcher = new QuoteSummaryFetcher(httpClient);
        Map<YahooMetric,Object> quotes = fetcher.getMetrics("PFE", List.of(CURRENCY, REGULAR_MARKET_TIME, REGULAR_MARKET_PRICE, TRAILING_PE));
        assertEquals("USD", quotes.get(CURRENCY));
        Object price = quotes.get(REGULAR_MARKET_PRICE);
        assertTrue(price instanceof BigDecimal);
        Object regularMarketTime = quotes.get(REGULAR_MARKET_TIME);
        assertTrue(regularMarketTime instanceof Long);
        Object trailingPE = quotes.get(TRAILING_PE);
        assertTrue(trailingPE instanceof BigDecimal);
    }
}
