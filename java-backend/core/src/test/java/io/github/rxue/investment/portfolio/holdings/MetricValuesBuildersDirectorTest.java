package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.lotsmatching.TradeMatcher;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.MetricValuesList;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;

import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.COST;
import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.POSITION;
import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.BUY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MetricValuesBuildersDirectorTest {
    private static final QuoteFetcher FAILING_QUOTE_FETCHER = new QuoteFetcher() {
        @Override
        public MetricValuesList fetchAll(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
            return fail("fetchAll should not be invoked");
        }
        @Override
        public Map<QuoteMetric, Comparable<?>> fetch(String securityId, Set<QuoteMetric> quoteMetrics) {
            return fail("fetch should not be invoked");
        }
    };

    @Test
    public void construct_withoutQuoteMetrics_doesNotFetchQuotes() {
        List<OpenLots> openLotsList = new TradeMatcher()
                .matchInFIFO(List.of(new Trade(LocalDate.of(2025, 5, 1), BUY, "PFE", 10, 16000)))
                .openLotsList();
        SequencedSet<Metric> metrics = new LinkedHashSet<>();
        metrics.add(POSITION);
        metrics.add(COST);

        Holdings holdings = new MetricValuesBuildersDirector(FAILING_QUOTE_FETCHER)
                .construct(metrics, openLotsList);

        assertEquals(1, holdings.rows().size());
        MetricValues pfe = holdings.rows().get(0);
        assertEquals("PFE", pfe.securityId());
        assertEquals(List.of(POSITION, COST), pfe.metricList());
        assertEquals(10, pfe.get(POSITION));
        assertEquals(openLotsList.get(0).costInCent(), pfe.get(COST));
    }
}
