package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.metric.Metric;

import java.util.Set;

import static io.github.rxue.investment.marketquote.DerivedQuoteMetric.LATEST_PRICE_IN_REPORT_CURRENCY;

public enum HoldingMetric implements Metric {
    POSITION("Position"),LATEST_MARKET_VALUE_IN_REPORT_CURRENCY("Latest Market Value in Report Currency", Set.of(LATEST_PRICE_IN_REPORT_CURRENCY));
    private final String label;
    private final Set<QuoteMetric> dependentQuoteMetrics;
    HoldingMetric(String label) {
        this(label, Set.of());
    }
    HoldingMetric(String label, Set<QuoteMetric> dependentQuoteMetrics) {
        this.label = label;
        this.dependentQuoteMetrics = dependentQuoteMetrics;
    }

    @Override
    public String label() {
        return label;
    }

    public Set<QuoteMetric> dependentQuoteMetrics() {
        return dependentQuoteMetrics;
    }
}
