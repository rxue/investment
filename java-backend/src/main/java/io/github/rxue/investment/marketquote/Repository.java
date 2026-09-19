package io.github.rxue.investment.marketquote;

import java.net.CookieManager;
import java.net.http.HttpClient;

public class Repository {
    private final QuoteSummaryFetcher quoteSummaryFetcher;

    public Repository() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.quoteSummaryFetcher = new QuoteSummaryFetcher(httpClient);
    }
}
