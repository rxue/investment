package io.github.rxue.investment.marketquote.yahoofinance;

import java.util.Map;

public record YahooMetricValues(Map<YahooMetric,Comparable<?>> metricToValue) {
    public <T> T get(YahooMetric metric, Class<T> type) {
        return type.cast(metricToValue.get(metric));
    }
    public Comparable<?> get(YahooMetric metric) {
        return metricToValue.get(metric);
    }
}
