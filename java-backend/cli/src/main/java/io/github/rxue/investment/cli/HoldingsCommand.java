package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import io.github.rxue.investment.adapter.Account;
import io.github.rxue.investment.adapter.CSVTransactionLoader;
import io.github.rxue.investment.portfolio.holdings.HoldingMetric;
import io.github.rxue.investment.portfolio.holdings.MetricValuesBuildersDirector;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.marketquote.yahoofinance.YahooFinanceQuoteFetcher;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Transaction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


@Command(name = "holdings", description = "Fetch holdings from a given csv file or directory storing csv files",
        mixinStandardHelpOptions = true)
public class HoldingsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The portfolio bank account, possible values include OP, NORDNET atm")
    private String accountName;

    @Parameters(index = "1", description = "metrics (delimited by comma) needed to be listed")
    private String metricNames;

    @Parameters(index = "2", description = "The Path of the CSV file or directory")
    private String csvFileOrDirectoryPath;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        Account account = Account.valueOf(accountName);
        CSVTransactionLoader transactionLoader = new CSVTransactionLoader(account, Path.of(csvFileOrDirectoryPath));
        List<Transaction> transactions = transactionLoader.load().transactionList();
        List<Trade> trades = transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        MetricValuesBuildersDirector director = new MetricValuesBuildersDirector(new YahooFinanceQuoteFetcher(httpClient,"EUR"), trades);
        SequencedSet<Metric> metrics = getMetrics(metricNames);
        List<MetricValues> metricValuesList = director.construct(metrics);
        printMetricValues(metrics, metricValuesList);
        return 0;
    }
    private static void printMetricValues(SequencedSet<Metric> metrics, List<MetricValues> values) {
        List<ColumnData<MetricValues>> columns = new ArrayList<>();
        columns.add(new Column().header("Ticker Symbol").with(MetricValues::securityId));
        for (Metric metric : metrics) {
            columns.add(new Column().header(metric.label())
                    .with(mv -> String.valueOf(mv.get(metric))));
        }
        System.out.println(AsciiTable.getTable(values, columns));
    }

    private static SequencedSet<Metric> getMetrics(String metricNames) {
        return Arrays.stream(metricNames.split(","))
                .map(HoldingsCommand::toMetric)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Metric toMetric(String name) {
        Optional<HoldingMetric> holdingMetricOptional = Arrays.stream(HoldingMetric.values())
                .filter(m -> m.name().equals(name))
                .findFirst();
        if (holdingMetricOptional.isPresent())
            return holdingMetricOptional.get();
        return toQuoteMetric(name);
    }
    static QuoteMetric toQuoteMetric(String name) {
        return Arrays.stream(QuoteMetric.values())
                .filter(m -> m.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric: " + name));
    }
}
