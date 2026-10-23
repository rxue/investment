package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;

import java.util.Set;


public enum HoldingMetric implements Metric {
    POSITION("Position"),LATEST_MARKET_VALUE_IN_REPORT_CURRENCY("Latest Market Value in Report Currency", Set.of(QuoteMetric.LATEST_MARKET_PRICE_IN_REPORT_CURRENCY));
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
