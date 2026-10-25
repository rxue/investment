package io.github.rxue.investment.financialstatement.notes;

import io.github.rxue.investment.portfolio.holdings.Holdings;
import io.github.rxue.investment.portfolio.holdings.MetricValuesBuildersDirector;
import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.marketquote.yahoofinance.YahooFinanceQuoteFetcher;
import io.github.rxue.investmentutility.vo.Metric;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.COST;
import static io.github.rxue.investment.portfolio.holdings.HoldingMetric.POSITION;
import static io.github.rxue.investmentutility.marketquote.QuoteMetric.COMPANY_NAME;

public class Investment {
    private final List<OpenLots> openLotsList;
    Investment(List<OpenLots> openLotsList) {
        this.openLotsList = openLotsList;
    }

    public Holdings holdings() {
        MetricValuesBuildersDirector holdingsBuilderDirector = new MetricValuesBuildersDirector(new YahooFinanceQuoteFetcher("EUR"));
        SequencedSet<Metric> metrics = new LinkedHashSet<>();
        metrics.add(COMPANY_NAME);
        metrics.add(POSITION);
        metrics.add(COST);
        return holdingsBuilderDirector.construct(metrics, openLotsList);
    }
}
