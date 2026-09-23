package io.github.rxue.investment.marketquote;

import java.util.Map;

public record MetricValues(String yahooTickerSymbol, Map<Metric,Object> values) {
}
