package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import io.github.rxue.investment.adapter.marketquote.yahoofinance.YahooFinanceRepository;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.metric.Metric;
import io.github.rxue.investment.vo.metric.MetricValues;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Command(name = "quotes", description = "Fetch metrics for ticker symbols (atm the Yahoo ticker symbol)",
        mixinStandardHelpOptions = true)
public class QuoteCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Comma-delimited metric names, e.g. LATEST_TIME,CURRENCY")
    private String metricsArg;

    @Parameters(index = "1", description = "Comma-delimited Yahoo ticker symbols, e.g. AAPL,MSFT")
    private String yahooTickerSymbolsArg;

    @Option(names = "--sort-by", description = "Metric name to sort the rows by (ascending), must be one of the given metrics")
    private String sortByArg;

    @Override
    public Integer call() {
        List<QuoteMetric> metrics = getMetrics(metricsArg);
        List<String> tickerSymbols = Arrays.stream(yahooTickerSymbolsArg.split(","))
                .toList();
        Map<String,Long> duplicates = getDuplicates(tickerSymbols);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate ticker symbols: " + String.join(", ", duplicates.keySet()));
        }

        Metric sortingMetric = sortByArg == null ? null : toMetric(sortByArg);
        if (sortingMetric != null && !metrics.contains(sortingMetric)) {
            throw new IllegalArgumentException("Sorting metric " + sortByArg + " is not one of the given metrics");
        }
        List<MetricValues> values = new YahooFinanceRepository("EUR").getMetricValues(tickerSymbols.stream().collect(toSet()), metrics);
        if (sortingMetric != null) {
            values = values.stream()
                    .sorted(comparatorByMetric(sortingMetric))
                    .toList();
        }
        printMetricValues(metrics.stream().map(Metric.class::cast).toList(), values);
        return 0;
    }
    private Map<String,Long> getDuplicates(List<String> tickerSymbols) {
        Map<String,Long> tickerSymbolsByCounter = tickerSymbols.stream()
                .collect(groupingBy(Function.identity(), HashMap::new, counting()));
        return tickerSymbolsByCounter.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static void printMetricValues(List<Metric> metrics, List<MetricValues> values) {
        List<ColumnData<MetricValues>> columns = new ArrayList<>();
        columns.add(new Column().header("Ticker Symbol").with(MetricValues::securityId));
        for (Metric metric : metrics) {
            columns.add(new Column().header(metric.label())
                    .with(mv -> String.valueOf(mv.values().get(metric))));
        }
        System.out.println(AsciiTable.getTable(values, columns));
    }

    private static List<QuoteMetric> getMetrics(String metricNames) {
        return Arrays.stream(metricNames.split(","))
                .map(QuoteCommand::toMetric)
                .toList();
    }
    static QuoteMetric toMetric(String name) {
        return QuoteMetric.all().stream()
                .filter(m -> m.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric: " + name));
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Comparator<MetricValues> comparatorByMetric(Metric metric) {
        return Comparator.comparing(
                (MetricValues mv) -> (Comparable) mv.values().get(metric),
                Comparator.nullsLast(Comparator.naturalOrder()));
    }
}
