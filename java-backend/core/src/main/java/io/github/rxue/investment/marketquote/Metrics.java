package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Metric;

import java.util.*;

import static java.util.stream.Collectors.toSet;

record Metrics(Collection<Metric> metrics) {
    public Set<QuoteMetric> quoteMetrics() {
        return metrics.stream()
                .filter(m -> m instanceof QuoteMetric)
                .map(m -> (QuoteMetric) m)
                .collect(toSet());
    }
    public Set<QuoteMetric> allNeededQuoteMetrics() {
        Set<QuoteMetric> quoteMetricCS = new HashSet<>(quoteMetrics());
        Set<QuoteMetric> dependentQuoteMetricCS = derivedMetrics().stream()
                .map(DerivedMetric::dependentQuoteMetrics)
                .flatMap(Set::stream)
                .collect(toSet());
        quoteMetricCS.addAll(dependentQuoteMetricCS);
        return Collections.unmodifiableSet(quoteMetricCS);
    }
    public List<DerivedMetric> derivedMetrics() {
        return metrics.stream()
                .filter(DerivedMetric.class::isInstance)
                .map(DerivedMetric.class::cast)
                .toList();
    }
}
