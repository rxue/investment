package io.github.rxue.investment.adapter.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.*;
import io.github.rxue.investment.vo.Metric;
import io.github.rxue.investment.vo.MetricValues;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.*;
import java.util.stream.Collectors;


public class YahooFinanceRepository extends AbstractRepository {
    private final QuoteSummaryFetcher quoteSummaryFetcher;
    public YahooFinanceRepository(String reportCurrency) {
        super(new FxRateFetcher(HttpClient.newHttpClient()), reportCurrency);
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.quoteSummaryFetcher = new QuoteSummaryFetcher(httpClient);
    }

    @Override
    protected MetricValues getQuoteMetrics(String securityId, Set<QuoteMetric> quoteMetrics) {
        Set<YahooMetric> yahooMetrics = quoteMetrics.stream()
                .map(YahooMetric::of)
                .collect(Collectors.toSet());
        Map<YahooMetric,Comparable<?>> yahooMetricValues = quoteSummaryFetcher.getValues(securityId, yahooMetrics);
        Map<Metric,Comparable<?>> result = new HashMap<>();
        for(QuoteMetric quoteMetric : quoteMetrics)
            result.put(quoteMetric, yahooMetricValues.get(YahooMetric.of(quoteMetric)));
        return new MetricValues(securityId, Collections.unmodifiableMap(result));
    }

}
