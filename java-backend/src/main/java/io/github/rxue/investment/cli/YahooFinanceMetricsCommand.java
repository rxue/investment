package io.github.rxue.investment.cli;

import io.github.rxue.investment.marketquote.QuoteSummaryFetcher;
import io.github.rxue.investment.marketquote.YahooMetric;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "yahoo_finance_metrics", description = "Fetch metrics for ticker symbols from Yahoo Finance",
        mixinStandardHelpOptions = true)
public class YahooFinanceMetricsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Comma-delimited metric names, e.g. TRAILING_PE,CURRENCY")
    private String metricsArg;

    @Parameters(index = "1", description = "Comma-delimited Yahoo ticker symbols, e.g. AAPL,MSFT")
    private String symbolsArg;

    @Override
    public Integer call() {
        Set<YahooMetric> metrics = Arrays.stream(metricsArg.split(","))
                .map(String::trim)
                .map(YahooMetric::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(YahooMetric.class)));
        List<String> symbols = Arrays.stream(symbolsArg.split(","))
                .map(String::trim)
                .toList();

        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        QuoteSummaryFetcher fetcher = new QuoteSummaryFetcher(httpClient);
        for (String symbol : symbols) {
            Map<YahooMetric, Object> quotes = fetcher.getMetrics(symbol, metrics);
            quotes.forEach((metric, value) -> System.out.println(symbol + " " + metric.getName() + " = " + value));
        }
        return 0;
    }
}
