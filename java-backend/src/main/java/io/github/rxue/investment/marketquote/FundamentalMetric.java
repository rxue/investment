package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;

public final class FundamentalMetric implements Metric {
    private static final Map<String, FundamentalMetric> ALL = new HashMap<>();
    public static final FundamentalMetric LATEST_PRICE = of("LATEST_PRICE", "Latest Price", Set.of(REGULAR_MARKET_PRICE, CURRENCY, REGULAR_MARKET_TIME, GMT_OFFSET_IN_MILLISECONDS));
    private final String name;
    private final String descriptiveName;
    private final Set<YahooMetric<?>> dependentYahooMetrics;
    private FundamentalMetric(String name, String descriptiveName, Set<YahooMetric<?>> dependentYahooMetrics) {
        this.name = name;
        this.descriptiveName = descriptiveName;
        this.dependentYahooMetrics = dependentYahooMetrics;
    }

    @Override
    public String name() {
        return descriptiveName;
    }

    public Set<YahooMetric<?>> dependentYahooMetrics() {
        return dependentYahooMetrics;
    }

    private static FundamentalMetric of(String name, String descriptiveName, Set<YahooMetric<?>> dependentYahooMetrics) {
        FundamentalMetric newMetric = new FundamentalMetric(name, descriptiveName, dependentYahooMetrics);
        ALL.put(name, newMetric);
        return newMetric;
    }

    public static FundamentalMetric get(String metricName) {
        return ALL.get(metricName);
    }

}
