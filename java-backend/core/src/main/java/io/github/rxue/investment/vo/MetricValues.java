package io.github.rxue.investment.vo;

import java.util.Map;

public record MetricValues(String securityId, Map<Metric,Comparable<?>> values) {
    public<T> T get(Metric metric, Class<T> valueType) {
        return valueType.cast(values.get(metric));
    }
    public Comparable<?> get(Metric metric) {
        return values.get(metric);
    }
}
