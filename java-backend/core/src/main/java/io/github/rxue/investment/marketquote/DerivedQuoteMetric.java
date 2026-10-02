package io.github.rxue.investment.marketquote;

import java.util.Set;

import static io.github.rxue.investment.marketquote.BaseQuoteMetric.CURRENCY;
import static io.github.rxue.investment.marketquote.BaseQuoteMetric.REGULAR_MARKET_PRICE;

public enum DerivedQuoteMetric implements QuoteMetric {
    LATEST_PRICE("Latest Price", Set.of(REGULAR_MARKET_PRICE, CURRENCY)),
    LATEST_PRICE_IN_REPORT_CURRENCY("Latest Price in Report Currency", Set.of(REGULAR_MARKET_PRICE, CURRENCY));
    private final String label;
    private final Set<BaseQuoteMetric> dependentQuoteMetrics;
    DerivedQuoteMetric(String label, Set<BaseQuoteMetric> dependentQuoteMetrics) {
        this.label = label;
        this.dependentQuoteMetrics = dependentQuoteMetrics;
    }

    public Set<BaseQuoteMetric> dependentBaseQuoteMetrics() {
        return dependentQuoteMetrics;
    }

    @Override
    public String label() {
        return label;
    }
}
