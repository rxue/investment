package io.github.rxue.investment.marketquote;

import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.rxue.investment.marketquote.FundamentalMetric.LATEST_PRICE;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryIT {
    @Test
    void getMetrics_with_both_yahoo_and_fundamental_metrics() {
        Repository repository = new Repository("EURO");
        List<MetricValues> result = repository.getMetrics(List.of("PFE","GOOG"), List.of(LATEST_PRICE, REGULAR_MARKET_TIME));
        assertTrue(result.size() > 0);
    }
}
