package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.vo.Metric;

public enum HoldingMetric implements Metric {
    POSITION("Position");
    private final String label;
    HoldingMetric(String label) {
        this.label = label;
    }

    @Override
    public String label() {
        return label;
    }
}
