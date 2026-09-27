package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Metric;

import java.util.Set;

import static io.github.rxue.investment.marketquote.QuoteMetric.CURRENCY;
import static io.github.rxue.investment.marketquote.QuoteMetric.REGULAR_MARKET_PRICE;

public enum DerivedMetric implements Metric {
    LATEST_PRICE("Latest Price", Set.of(REGULAR_MARKET_PRICE, CURRENCY)),
    LATEST_PRICE_IN_REPORT_CURRENCY("Latest Price in Report Currency", Set.of(REGULAR_MARKET_PRICE, CURRENCY));
    private final String label;
    private final Set<QuoteMetric> dependentQuoteMetrics;
    DerivedMetric(String label, Set<QuoteMetric> dependentQuoteMetrics) {
        this.label = label;
        this.dependentQuoteMetrics = dependentQuoteMetrics;
    }

    public Set<QuoteMetric> dependentQuoteMetrics() {
        return dependentQuoteMetrics;
    }

    @Override
    public String label() {
        return label;
    }
}
