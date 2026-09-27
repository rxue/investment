package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;
import io.github.rxue.investment.vo.Metric;

import java.util.Set;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;

public enum FundamentalMetric implements Metric {
    LATEST_PRICE("Latest Price", Set.of(REGULAR_MARKET_PRICE, CURRENCY)),
    LATEST_PRICE_IN_REPORT_CURRENCY("Latest Price in Report Currency", Set.of(REGULAR_MARKET_PRICE, CURRENCY));
    private final String label;
    private final Set<YahooMetric> dependentYahooMetrics;
    FundamentalMetric(String label, Set<YahooMetric> dependentYahooMetrics) {
        this.label = label;
        this.dependentYahooMetrics = dependentYahooMetrics;
    }

    public Set<YahooMetric> dependentYahooMetrics() {
        return dependentYahooMetrics;
    }

}
