package io.github.rxue.investment.marketquote.yahoofinance;

import java.util.Map;

public record YahooMetricValues(Map<YahooMetric<?>,Object> metricToValue) {
    public <T> T get(YahooMetric<T> metric) {
        Object value = metricToValue.get(metric);
        return (T) value;
    }
}
