package io.github.rxue.investment.vo;

import java.util.Map;

public record MetricValues(String tickerSymbol, Map<Metric,Comparable<?>> values) {
}
