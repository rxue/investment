package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.metric.Metric;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface QuoteMetric extends Metric {
    static Set<QuoteMetric> all() {
        Set<QuoteMetric> allQuoteMetrics = new HashSet<>();
        allQuoteMetrics.addAll(Set.of(BaseQuoteMetric.values()));
        allQuoteMetrics.addAll(Set.of(DerivedQuoteMetric.values()));
        return Collections.unmodifiableSet(allQuoteMetrics);
    }
}
