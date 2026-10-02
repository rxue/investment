package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.metric.MetricValues;

import java.util.*;

public interface Repository {

    List<MetricValues> getMetricValues(Collection<String> securityIds, Collection<QuoteMetric> requiredMetrics);
    default Map<String,MetricValues> getMetricValuesBySecurityId(Collection<String> securityIds, Collection<QuoteMetric> requiredMetrics) {
        Map<String,MetricValues> securityIdsToMetricValues = new LinkedHashMap<>();
        for (MetricValues metricValues : getMetricValues(securityIds, requiredMetrics)) {
            securityIdsToMetricValues.put(metricValues.securityId(), metricValues);
        }
        return Collections.unmodifiableMap(securityIdsToMetricValues);
    }


}
