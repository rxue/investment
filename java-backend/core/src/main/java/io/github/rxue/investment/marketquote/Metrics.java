package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;

import java.util.*;

import static java.util.stream.Collectors.toSet;

record Metrics(Collection<Metric> metrics) {
    Set<YahooMetric> yahooMetrics() {
        return metrics.stream()
                .filter(m -> m instanceof YahooMetric)
                .map(m -> (YahooMetric) m)
                .collect(toSet());
    }
    Set<YahooMetric> allNeededYahooMetrics() {
        Set<YahooMetric> yahooMetricCS = new HashSet<>(yahooMetrics());
        Set<YahooMetric> dependentYahooMetricCS = (Set<YahooMetric>) fundamentalMetrics().stream()
                .map(FundamentalMetric::dependentYahooMetrics)
                .flatMap(Set::stream)
                .collect(toSet());
        yahooMetricCS.addAll(dependentYahooMetricCS);
        return Collections.unmodifiableSet(yahooMetricCS);
    }
    List<FundamentalMetric> fundamentalMetrics() {
        return metrics.stream()
                .filter(FundamentalMetric.class::isInstance)
                .map(FundamentalMetric.class::cast)
                .toList();
    }
}
