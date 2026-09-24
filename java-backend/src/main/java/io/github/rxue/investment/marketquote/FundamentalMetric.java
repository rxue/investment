package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;

import java.util.Set;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;

public enum FundamentalMetric implements Metric {
    LATEST_PRICE("Latest Price", Set.of(REGULAR_MARKET_PRICE, CURRENCY, REGULAR_MARKET_TIME, GMT_OFFSET_IN_MILLISECONDS));
    private final String descriptiveName;
    private final Set<YahooMetric> dependentYahooMetricCS;
    private FundamentalMetric(String descriptiveName, Set<YahooMetric> dependentYahooMetricCS) {
        this.descriptiveName = descriptiveName;
        this.dependentYahooMetricCS = dependentYahooMetricCS;
    }

    public Set<YahooMetric> dependentYahooMetrics() {
        return dependentYahooMetricCS;
    }

}
