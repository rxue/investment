package io.github.rxue.investment.portfolio.holdings;


import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.MetricValuesList;
import io.github.rxue.investmentutility.vo.QuotePrice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.Function;

import static io.github.rxue.investmentutility.marketquote.QuoteMetric.LATEST_MARKET_PRICE_IN_REPORT_CURRENCY;
import static java.util.stream.Collectors.toMap;


public class MetricValuesBuildersDirector {
    private final QuoteFetcher marketQuoteSearchService;
    private Map<String,Holding> holdings;
    private final Map<String, MetricValues.Builder> metricValuesBuilders;
    private MetricValuesList quoteMetricValuesList;
    public MetricValuesBuildersDirector(QuoteFetcher marketQuoteSearchService) {
        this.marketQuoteSearchService = marketQuoteSearchService;
        this.metricValuesBuilders = new HashMap<>();

    }
    public Holdings construct(SequencedSet<Metric> metrics, List<OpenLots> openLotsList) {
        holdings = openLotsList.stream()
                .map(Holding::of)
                .collect(toMap(Holding::securityId, Function.identity()));
        final Set<QuoteMetric> allQuoteMetrics = new HoldingMetrics(metrics)
                .allQuoteMetrics();
        final Set<String> securityIds = holdings.keySet();
        quoteMetricValuesList = allQuoteMetrics.isEmpty()
                ? new MetricValuesList(Map.of())
                : marketQuoteSearchService.fetchAll(securityIds, allQuoteMetrics);
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
