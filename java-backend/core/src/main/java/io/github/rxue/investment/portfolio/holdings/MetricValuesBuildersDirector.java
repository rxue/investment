package io.github.rxue.investment.portfolio.holdings;


import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.MetricValuesList;
import io.github.rxue.investmentutility.vo.QuotePrice;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;

import static io.github.rxue.investmentutility.marketquote.QuoteMetric.LATEST_MARKET_PRICE_IN_REPORT_CURRENCY;


public class MetricValuesBuildersDirector {
    private final QuoteFetcher marketQuoteSearchService;
    private final Map<String,Holding> holdings;
    private final Map<String, MetricValues.Builder> metricValuesBuilders;
    private MetricValuesList quoteMetricValuesList;
    public MetricValuesBuildersDirector(QuoteFetcher marketQuoteSearchService, List<Trade> trades) {
        this.marketQuoteSearchService = marketQuoteSearchService;
        this.holdings = new HoldingsBuilder()
                .apply(trades)
                .buildMap();
        this.metricValuesBuilders = new HashMap<>();

    }
    public Holdings construct(SequencedSet<Metric> metrics) {
        final Set<QuoteMetric> allQuoteMetrics = new HoldingMetrics(metrics)
                .allQuoteMetrics();
        final Set<String> securityIds = holdings.keySet();
        quoteMetricValuesList = marketQuoteSearchService.fetchAll(securityIds, allQuoteMetrics);
        for (Metric metric : metrics) {
            securityIds.forEach(securityId -> {
                MetricValues.Builder metricValuesBuilder = metricValuesBuilders.computeIfAbsent(securityId, secId -> new MetricValues.Builder(secId));
                metricValuesBuilder.add(metric, getMetricValue(securityId, metric));
            });
        }
        List<MetricValues> metricValues = metricValuesBuilders.values().stream()
                .map(MetricValues.Builder::build)
                .toList();
        return new Holdings(metricValues);
    }
    private Comparable<?> getMetricValue(String securityId, Metric metric) {
        Holding holding = holdings.get(securityId);
        final int position = holding.position();
        return switch(metric) {
            case HoldingMetric.POSITION -> Integer.valueOf(position);
            case HoldingMetric.COST -> holding.costInCent();
            case HoldingMetric.LATEST_MARKET_VALUE_IN_REPORT_CURRENCY -> {
                QuotePrice latestPriceInReportCurrency = quoteMetricValuesList.getMetricValue(securityId, LATEST_MARKET_PRICE_IN_REPORT_CURRENCY, QuotePrice.class);
                yield latestPriceInReportCurrency.valueInCent() * position;
            }
            case QuoteMetric quoteMetric -> quoteMetricValuesList.getMetricValue(securityId, quoteMetric, Comparable.class);
            default -> throw new IllegalStateException("Unexpected value: " + metric);
        };
    }
}
