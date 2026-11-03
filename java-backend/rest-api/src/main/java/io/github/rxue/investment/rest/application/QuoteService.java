package io.github.rxue.investment.rest.application;

import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuoteService {
    private final QuoteFetcher marketQuoteFetcher;

    public QuoteService(QuoteFetcher marketQuoteFetcher) {
        this.marketQuoteFetcher = marketQuoteFetcher;
    }

    public List<MetricValues> getQuoteMetricValuesList(Set<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        return marketQuoteFetcher.fetchAll(securityIds, quoteMetrics).valuesList();
    }

    private static MetricValuesDTO toMetricValuesDTO(Map.Entry<String,Map<Metric,Comparable<?>>> entry) {
        return new MetricValuesDTO(entry.getKey(), entry.getValue());
    }

}
