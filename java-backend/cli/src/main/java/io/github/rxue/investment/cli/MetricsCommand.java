package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import io.github.rxue.investment.adapter.marketquote.yahoofinance.YahooFinanceRepository;
import io.github.rxue.investment.marketquote.DerivedMetric;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.Metric;
import io.github.rxue.investment.vo.MetricValues;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(name = "metrics", description = "Fetch metrics for ticker symbols (atm the Yahoo ticker symbol)",
        mixinStandardHelpOptions = true)
public class MetricsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Comma-delimited metric names, e.g. LATEST_TIME,CURRENCY")
    private String metricsArg;

    @Parameters(index = "1", description = "Comma-delimited Yahoo ticker symbols, e.g. AAPL,MSFT")
    private String tickerSymbolsArg;

    @Option(names = "--sort-by", description = "Metric name to sort the rows by (ascending), must be one of the given metrics")
    private String sortByArg;

    @Override
    public Integer call() {
        List<Metric> metrics = getMetrics(metricsArg);
        List<String> tickerSymbols = Arrays.stream(tickerSymbolsArg.split(","))
                .toList();
        Metric sortingMetric = sortByArg == null ? null : toMetric(sortByArg);
        if (sortingMetric != null && !metrics.contains(sortingMetric)) {
            throw new IllegalArgumentException("Sorting metric " + sortByArg + " is not one of the given metrics");
        }
        List<MetricValues> values = new YahooFinanceRepository("EUR").getMetrics(tickerSymbols, metrics);
        if (sortingMetric != null) {
            values = values.stream()
                    .sorted(comparatorByMetric(sortingMetric))
                    .toList();
        }

        List<ColumnData<MetricValues>> columns = new ArrayList<>();
        columns.add(new Column().header("Ticker Symbol").with(MetricValues::securityId));
        for (Metric metric : metrics) {
            columns.add(new Column().header(metric.label())
                    .with(mv -> String.valueOf(mv.values().get(metric))));
        }
        System.out.println(AsciiTable.getTable(values, columns));

        return 0;
    }
    private static List<Metric> getMetrics(String metricNames) {
        return Arrays.stream(metricNames.split(","))
                .map(MetricsCommand::toMetric)
                .toList();
    }
    private static Metric toMetric(String name) {
        return Stream.of(QuoteMetric.values(), DerivedMetric.values())
                .flatMap(Arrays::stream)
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
