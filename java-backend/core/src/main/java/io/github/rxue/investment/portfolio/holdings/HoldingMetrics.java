package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.metric.Metric;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

record HoldingMetrics(Collection<Metric> metrics) {
    /**
     * all quote metrics includes both QuoteMetric directly needed by the client and those needed by some HoldingMetric
     *
     * @return
     */
    Set<QuoteMetric> allQuoteMetrics() {
        Set<QuoteMetric> directQuoteMetrics = metrics.stream()
                .filter(QuoteMetric.class::isInstance)
                .map(QuoteMetric.class::cast)
                .collect(toSet());
        Set<QuoteMetric> dependentQuoteMetrics = metrics.stream()
                .filter(HoldingMetric.class::isInstance)
                .map(HoldingMetric.class::cast)
                .map(HoldingMetric::dependentQuoteMetrics)
                .flatMap(Set::stream)
                .collect(toSet());
        Set<QuoteMetric> allQuoteMetrics = new HashSet<>(directQuoteMetrics);
        allQuoteMetrics.addAll(dependentQuoteMetrics);
        return Collections.unmodifiableSet(allQuoteMetrics);
    }
}
