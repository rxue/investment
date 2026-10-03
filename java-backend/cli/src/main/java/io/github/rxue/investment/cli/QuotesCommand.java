package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import io.github.rxue.investment.adapter.marketquote.yahoofinance.YahooFinanceRepository;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.metric.Metric;
import io.github.rxue.investment.vo.metric.MetricValues;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Command(name = "quotes", description = "Fetch metrics for ticker symbols (atm the Yahoo ticker symbol)",
        mixinStandardHelpOptions = true)
public class QuotesCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Comma-delimited metric names, e.g. LATEST_TIME,CURRENCY")
    private String metricsArg;

    @Parameters(index = "1", description = "Either comma-delimited Yahoo ticker symbols, e.g. AAPL,MSFT, or a csv file path")
    private String yahooTickerSymbolsArg;

    @Option(names = "--sort-by", description = "Metric name to sort the rows by (ascending), must be one of the given metrics")
    private String sortByArg;

    @Option(names = "--threads", description = "Amount of threads to use in the fetch of quote")
    private int nThreads = 1;

    @Override
    public Integer call() {
        List<QuoteMetric> metrics = getMetrics(metricsArg);
        List<String> tickerSymbols = getTickerSymbols(yahooTickerSymbolsArg);
        Map<String,Long> duplicates = getDuplicates(tickerSymbols);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate ticker symbols: " + String.join(", ", duplicates.keySet()));
        }

        Metric sortingMetric = sortByArg == null ? null : toMetric(sortByArg);
        if (sortingMetric != null && !metrics.contains(sortingMetric)) {
            throw new IllegalArgumentException("Sorting metric " + sortByArg + " is not one of the given metrics");
        }
        List<MetricValues> values;
        try(ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            long start = System.nanoTime();
            YahooFinanceRepository repository = new YahooFinanceRepository(executorService, "EUR");
            values = repository.getMetricValues(tickerSymbols.stream().collect(toSet()), metrics);
            System.out.println("getMetricValues took " + (System.nanoTime() - start) / 1_000_000 + " ms");
        }

        if (sortingMetric != null) {
            values = values.stream()
                    .sorted(comparatorByMetric(sortingMetric))
                    .toList();
        }
        printMetricValues(metrics.stream().map(Metric.class::cast).toList(), values);
        return 0;
    }
    private static List<String> getTickerSymbols(String symbolsOrCsvPath) {
        if (symbolsOrCsvPath.contains(",") || !symbolsOrCsvPath.endsWith(".csv")) {
            return Arrays.stream(symbolsOrCsvPath.split(","))
                    .toList();
        }
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();
        try (CSVParser parser = CSVParser.parse(Path.of(symbolsOrCsvPath), StandardCharsets.UTF_8, format)) {
            List<String> tickerSymbols = new ArrayList<>();
            for (CSVRecord record : parser) {
                tickerSymbols.add(record.get("yahoo_ticker_symbol"));
            }
            return Collections.unmodifiableList(tickerSymbols);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + symbolsOrCsvPath, e);
        }
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
                .map(QuotesCommand::toMetric)
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
