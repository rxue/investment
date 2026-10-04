package io.github.rxue.investment.cli;

import io.github.rxue.investment.adapter.Account;
import io.github.rxue.investment.adapter.CSVTransactionLoader;
import io.github.rxue.investment.adapter.marketquote.yahoofinance.YahooFinanceRepository;
import io.github.rxue.investment.portfolio.holdings.HoldingMetric;
import io.github.rxue.investment.portfolio.holdings.MetricValuesBuildersDirector;
import io.github.rxue.investment.portfolio.transactions.Trade;
import io.github.rxue.investment.portfolio.transactions.Transaction;
import io.github.rxue.investment.vo.metric.Metric;
import io.github.rxue.investment.vo.metric.MetricValues;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.github.rxue.investment.cli.QuotesCommand.printMetricValues;

@Command(name = "holdings", description = "Fetch holdings from a given csv file or directory storing csv files",
        mixinStandardHelpOptions = true)
public class HoldingsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The portfolio bank account, possible values include OP, NORDNET atm")
    private String accountName;

    @Parameters(index = "1", description = "metrics (delimited by comma) needed to be listed")
    private String metricNames;

    @Parameters(index = "2", description = "The Path of the CSV file or directory")
    private String csvFileOrDirectoryPath;

    @CommandLine.Option(names = "--threads", description = "Amount of threads to use in the fetch of quote")
    private int nThreads;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        Account account = Account.valueOf(accountName);
        CSVTransactionLoader transactionLoader = new CSVTransactionLoader(account, Path.of(csvFileOrDirectoryPath));
        List<Transaction> transactions = transactionLoader.load();
        List<Trade> trades = transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        MetricValuesBuildersDirector director = new MetricValuesBuildersDirector(new YahooFinanceRepository("EUR"), trades);
        SequencedSet<Metric> metrics = getMetrics(metricNames);
        List<MetricValues> metricValuesList = director.construct(metrics);
        printMetricValues(metrics.stream().toList(), metricValuesList);
        return 0;
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
        return QuotesCommand.toMetric(name);
    }

}
