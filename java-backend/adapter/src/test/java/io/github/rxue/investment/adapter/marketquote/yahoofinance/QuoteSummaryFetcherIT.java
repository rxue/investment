package io.github.rxue.investment.adapter.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuoteSummaryFetcherIT {
    @Test
    public void getValues_nonExistingTickerSymbol() {
        try (HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new CookieManager()).build()) {
            QuoteSummaryFetcher fetcher = new QuoteSummaryFetcher(httpClient);
            Map<YahooMetric,Comparable<?>> values = fetcher.getValues("NOSUCHSYMBOLX", Set.of(YahooMetric.REGULAR_MARKET_PRICE));
            assertEquals(Map.of(), values);
        }
    }
}
