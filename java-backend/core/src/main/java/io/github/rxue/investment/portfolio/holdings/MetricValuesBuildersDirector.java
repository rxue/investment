package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.portfolio.transactions.Trade;
import io.github.rxue.investment.vo.metric.Metric;
import io.github.rxue.investment.vo.metric.MetricValues;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;

import static io.github.rxue.investment.marketquote.DerivedQuoteMetric.LATEST_PRICE_IN_REPORT_CURRENCY;

public class MetricValuesBuildersDirector {
    private final Repository marketQuoteRepository;
    private final Map<String,Holding> holdings;
    private final Map<String,MetricValues.Builder> metricValuesBuilders;
    private Map<String,MetricValues> allQuoteMetricValues;
    public MetricValuesBuildersDirector(Repository marketQuoteRepository, List<Trade> trades) {
        this.marketQuoteRepository = marketQuoteRepository;
        this.holdings = new HoldingsBuilder()
                .apply(trades)
                .buildMap();
        this.metricValuesBuilders = new HashMap<>();

    }
    public List<MetricValues> construct(SequencedSet<Metric> metrics) {
        final Set<QuoteMetric> allQuoteMetrics = new HoldingMetrics(metrics)
                .allQuoteMetrics();
        final Set<String> securityIds = holdings.keySet();
        allQuoteMetricValues = marketQuoteRepository.getMetricValuesBySecurityId(securityIds, allQuoteMetrics);
        for (Metric metric : metrics) {
            securityIds.forEach(securityId -> {
                MetricValues.Builder metricValuesBuilder = metricValuesBuilders.computeIfAbsent(securityId, secId -> new MetricValues.Builder(secId));
                metricValuesBuilder.add(metric, getMetricValue(securityId, metric));
            });
        }
        return metricValuesBuilders.values().stream()
                .map(MetricValues.Builder::build)
                .toList();
    }
    private Comparable<?> getMetricValue(String securityId, Metric metric) {
        final int position = holdings.get(securityId).position();
        return switch(metric) {
            case HoldingMetric.POSITION -> Integer.valueOf(position);
            case HoldingMetric.LATEST_MARKET_VALUE_IN_REPORT_CURRENCY -> {
                BigDecimal latestPriceInReportCurrency = allQuoteMetricValues.get(securityId)
                        .get(LATEST_PRICE_IN_REPORT_CURRENCY, BigDecimal.class);
                yield latestPriceInReportCurrency.multiply(BigDecimal.valueOf(position));
            }
            case QuoteMetric quoteMetric -> allQuoteMetricValues.get(securityId).get(quoteMetric);
            default -> throw new IllegalStateException("Unexpected value: " + metric);
        };
    }
}
