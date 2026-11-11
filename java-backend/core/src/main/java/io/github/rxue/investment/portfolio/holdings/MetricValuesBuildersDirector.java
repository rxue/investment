package io.github.rxue.investment.portfolio.holdings;


import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.QuotePrice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.function.Function;

import static io.github.rxue.investmentutility.marketquote.QuoteMetric.REGULAR_MARKET_PRICE_IN_REPORT_CURRENCY;
import static java.util.stream.Collectors.toMap;


public class MetricValuesBuildersDirector {
    private final QuoteFetcher marketQuoteSearchService;
    private SequencedMap<String,Holding> holdings;
    private final SequencedMap<String, MetricValues.Builder> metricValuesBuilders;
    private List<MetricValues> quoteMetricValuesList;
    public MetricValuesBuildersDirector(QuoteFetcher marketQuoteSearchService) {
        this.marketQuoteSearchService = marketQuoteSearchService;
        this.metricValuesBuilders = new LinkedHashMap<>();

    }
    public Holdings construct(SequencedSet<Metric> metrics, List<OpenLots> openLotsList) {
        holdings = openLotsList.stream()
                .map(Holding::of)
                .collect(toMap(Holding::securityId,
                        Function.identity(),
                        (h1, h2) -> { throw new IllegalStateException("Duplicate securityId: " + h1.securityId()); },
                        LinkedHashMap::new));
        final SequencedSet<QuoteMetric> allQuoteMetrics = new HoldingMetrics(metrics)
                .allQuoteMetrics();
        final SequencedSet<String> securityIds = holdings.sequencedKeySet();
        quoteMetricValuesList = allQuoteMetrics.isEmpty()
                ? List.of() : marketQuoteSearchService.fetchAll(securityIds, allQuoteMetrics);
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
        Map<String,MetricValues> metricValusBySecurityId = quoteMetricValuesList.stream()
                .collect(toMap(MetricValues::getSecurityId, Function.identity()));
        Holding holding = holdings.get(securityId);
        final int position = holding.position();
        return switch(metric) {
            case HoldingMetric.POSITION -> Integer.valueOf(position);
            case HoldingMetric.COST -> holding.costInCent();
            case HoldingMetric.LATEST_MARKET_VALUE_IN_REPORT_CURRENCY -> {
                QuotePrice latestPriceInReportCurrency = (QuotePrice) metricValusBySecurityId.get(securityId).valueOf(REGULAR_MARKET_PRICE_IN_REPORT_CURRENCY);
                yield latestPriceInReportCurrency.valueInCent() * position;
            }
            case QuoteMetric quoteMetric -> metricValusBySecurityId.get(securityId).valueOf(quoteMetric);
            default -> throw new IllegalStateException("Unexpected value: " + metric);
        };
    }
}
