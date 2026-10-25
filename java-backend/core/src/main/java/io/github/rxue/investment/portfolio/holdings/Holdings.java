package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;

import java.math.BigDecimal;
import java.util.List;

import static io.github.rxue.investment.Util.toBaseUnit;
import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.COST;
import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.LATEST_MARKET_VALUE_IN_REPORT_CURRENCY;

public record Holdings(List<MetricValues> rows) {
    private BigDecimal totalCost() {
        if (!has(COST)) throw new UnsupportedOperationException();
        long totalCostInCent = rows.stream()
                .mapToLong(metricValues -> (Long) metricValues.get(COST))
                .sum();
        return toBaseUnit(totalCostInCent);
    }
    private BigDecimal totalMarketValue() {
        if (!has(LATEST_MARKET_VALUE_IN_REPORT_CURRENCY)) throw new UnsupportedOperationException();
        long totalCostInCent = rows.stream()
                .mapToLong(metricValues -> (Long) metricValues.get(LATEST_MARKET_VALUE_IN_REPORT_CURRENCY))
                .sum();
        return toBaseUnit(totalCostInCent);
    }
    public Summary summary() {
        return new Summary(totalCost(), totalMarketValue());
    }
    boolean has(Metric metric) {
        return rows.get(0).metricList().contains(metric);
    }
}
