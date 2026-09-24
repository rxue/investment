package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.List;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;
import static org.junit.jupiter.api.Assertions.*;

class QuoteSummaryFetcherIT {
    @Test
    void getNodeByModule_multiple_modules() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        QuoteSummaryFetcher fetcher = new QuoteSummaryFetcher(httpClient);
        YahooMetricValues result = fetcher.getValues("PFE", List.of(CURRENCY, REGULAR_MARKET_PRICE));
        assertEquals("USD", result.get(CURRENCY));
        assertInstanceOf(BigDecimal.class, result.get(REGULAR_MARKET_PRICE, YahooNumber.class).raw());
    }
}
