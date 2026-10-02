package io.github.rxue.investment.marketquote;


import java.util.*;
import static java.util.stream.Collectors.toSet;

public record QuoteMetrics(Collection<QuoteMetric> metrics) {
    public Set<BaseQuoteMetric> baseQuoteMetrics() {
        return metrics.stream()
                .filter(m -> m instanceof BaseQuoteMetric)
                .map(m -> (BaseQuoteMetric) m)
                .collect(toSet());
    }
    public Set<BaseQuoteMetric> allBaseQuoteMetrics() {
        Set<BaseQuoteMetric> quoteMetricCS = new HashSet<>(baseQuoteMetrics());
        Set<BaseQuoteMetric> dependentQuoteMetricCS = derivedMetrics().stream()
                .map(DerivedQuoteMetric::dependentBaseQuoteMetrics)
                .flatMap(Set::stream)
                .collect(toSet());
        quoteMetricCS.addAll(dependentQuoteMetricCS);
        return Collections.unmodifiableSet(quoteMetricCS);
    }
    public List<DerivedQuoteMetric> derivedMetrics() {
        return metrics.stream()
                .filter(DerivedQuoteMetric.class::isInstance)
                .map(DerivedQuoteMetric.class::cast)
                .toList();
    }
}
