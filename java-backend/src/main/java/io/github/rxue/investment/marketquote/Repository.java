package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.QuoteSummaryFetcher;
import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;
import io.github.rxue.investment.marketquote.yahoofinance.YahooMetricValues;
import io.github.rxue.investment.marketquote.yahoofinance.YahooNumber;
import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.*;

import static io.github.rxue.investment.marketquote.FundamentalMetric.LATEST_PRICE;
import static io.github.rxue.investment.marketquote.FundamentalMetric.LATEST_PRICE_IN_REPORT_CURRENCY;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;

public class Repository {
    private final QuoteSummaryFetcher quoteSummaryFetcher;
    private final FxRateFetcher fxRateFetcher;
    private final String reportCurrency;
    public Repository(String reportCurrency) {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.quoteSummaryFetcher = new QuoteSummaryFetcher(httpClient);
        this.fxRateFetcher = new FxRateFetcher(HttpClient.newHttpClient());
        this.reportCurrency = reportCurrency;
    }

    public List<MetricValues> getMetrics(Collection<String> yahooTickerSymbols, Collection<Metric> requiredMetrics) {
        final Metrics metrics = new Metrics(requiredMetrics);
        return yahooTickerSymbols.stream()
                .map(s -> new MetricValues(s, getSingleStockMetrics(s, metrics)))
                .toList();
    }

    private Map<Metric,Comparable<?>> getSingleStockMetrics(String yahooTickerSymbol, Metrics metric) {
        Set<YahooMetric> allNeededYahooMetricCS = metric.allNeededYahooMetrics();
        YahooMetricValues allNeededYahooMetricValues = quoteSummaryFetcher.getValues(yahooTickerSymbol, allNeededYahooMetricCS);
        Map<Metric,Comparable<?>> result = new HashMap<>();
        for (FundamentalMetric fundamentalMetric : metric.fundamentalMetrics()) {
            if (fundamentalMetric == LATEST_PRICE) {
                Price price = getLatestPrice(allNeededYahooMetricValues);
                result.put(LATEST_PRICE, price);
            } else if (fundamentalMetric == LATEST_PRICE_IN_REPORT_CURRENCY) {
                result.put(LATEST_PRICE_IN_REPORT_CURRENCY, getLatestPriceInReportCurrency(allNeededYahooMetricValues));
            }
        }
        for (YahooMetric yahooMetric : metric.yahooMetrics()) {
            result.put(yahooMetric, allNeededYahooMetricValues.get(yahooMetric));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Price getLatestPrice(YahooMetricValues allNeededYahooMetricValues) {
        BigDecimal priceValue = allNeededYahooMetricValues.get(REGULAR_MARKET_PRICE, YahooNumber.class).raw();
        String currency = allNeededYahooMetricValues.get(CURRENCY, String.class);
        //long epoSeconds = allNeededYahooMetricValues.get(REGULAR_MARKET_TIME);
        //long gmtOffsetInMilliseconds = allNeededYahooMetricValues.get(GMT_OFFSET_IN_MILLISECONDS);
        //ZonedDateTime timestamp = Instant.ofEpochSecond(epoSeconds)
        //    .atZone(ZoneOffset.ofTotalSeconds((int) (gmtOffsetInMilliseconds / 1000)));
        Price price = new Price(priceValue.movePointRight(2).longValue(), currency);
        return price;
    }
    private BigDecimal getLatestPriceInReportCurrency(YahooMetricValues allNeededYahooMetricValues) {
        final String currency = allNeededYahooMetricValues.get(CURRENCY, String.class);
        final BigDecimal priceValue = allNeededYahooMetricValues.get(REGULAR_MARKET_PRICE, YahooNumber.class).raw();
        if (currency.equals(reportCurrency)) {
            return toMoneyValue(priceValue);
        }
        Map.Entry<LocalDate,BigDecimal> originalCurrencyFxRateFromEuro = fxRateFetcher.getFxRateFromEuro(currency, LocalDate.now());
        Map.Entry<LocalDate,BigDecimal> currencyFxRateFromEuro = fxRateFetcher.getFxRateFromEuro(reportCurrency, LocalDate.now());
        BigDecimal fxRate = originalCurrencyFxRateFromEuro.getValue().divide(currencyFxRateFromEuro.getValue(), MathContext.DECIMAL64);
        return toMoneyValue(priceValue.divide(fxRate, MathContext.DECIMAL64));
    }

    private static BigDecimal toMoneyValue(BigDecimal priceValue) {
        return priceValue.setScale(2, RoundingMode.HALF_UP);
    }


}
