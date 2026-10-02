package io.github.rxue.investment.adapter.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.*;
import io.github.rxue.investment.vo.metric.Metric;
import io.github.rxue.investment.vo.metric.MetricValues;

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
    protected MetricValues getBaseMetrics(String securityId, Set<BaseQuoteMetric> baseQuoteMetrics) {
        Set<YahooMetric> yahooMetrics = baseQuoteMetrics.stream()
                .map(YahooMetric::of)
                .collect(Collectors.toSet());
        Map<YahooMetric,Comparable<?>> yahooMetricValues = quoteSummaryFetcher.getValues(securityId, yahooMetrics);
        SequencedMap<Metric,Comparable<?>> result = new LinkedHashMap<>();
        for(BaseQuoteMetric baseQuoteMetric : baseQuoteMetrics)
            result.put(baseQuoteMetric, yahooMetricValues.get(YahooMetric.of(baseQuoteMetric)));
        return new MetricValues(securityId, Collections.unmodifiableSequencedMap(result));
    }

}
