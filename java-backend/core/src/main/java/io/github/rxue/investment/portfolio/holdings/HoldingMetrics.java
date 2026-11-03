package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;

import java.util.*;

import static java.util.stream.Collectors.toSet;

record HoldingMetrics(Collection<Metric> metrics) {
    /**
     * all quote metrics includes both QuoteMetric directly needed by the client and those needed by some HoldingMetric
     *
     * @return
     */
    SequencedSet<QuoteMetric> allQuoteMetrics() {
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
        SequencedSet<QuoteMetric> allQuoteMetrics = new LinkedHashSet<>(directQuoteMetrics);
        allQuoteMetrics.addAll(dependentQuoteMetrics);
        return Collections.unmodifiableSequencedSet(allQuoteMetrics);
    }
}
