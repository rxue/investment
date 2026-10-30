package io.github.rxue.investment.rest.api;

import io.github.rxue.investmentutility.vo.Metric;
import java.util.Map;

public record MetricValuesDTO(String securityId, Map<Metric,Comparable<?>> valueByMetric) {
}
