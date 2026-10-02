package io.github.rxue.investment.vo.metric;

import java.util.*;

public record MetricValues(String securityId, SequencedMap<Metric,Comparable<?>> values) {
    public<T> T get(Metric metric, Class<T> valueType) {
        return valueType.cast(values.get(metric));
    }
    public Comparable<?> get(Metric metric) {
        return values.get(metric);
    }
    public static class Builder {
        private final String securityId;
        private final SequencedMap<Metric,Comparable<?>> values;
        public Builder(String securityId) {
            this.securityId = securityId;
            this.values = new LinkedHashMap<>();
        }
        public Builder add(Metric metric, Comparable<?> value) {
            values.put(metric, value);
            return this;
        }
        public MetricValues build() {
            SequencedMap<Metric, Comparable<?>> values = new LinkedHashMap<>(this.values);
            return new MetricValues(securityId, Collections.unmodifiableSequencedMap(values));
        }
    }
}
