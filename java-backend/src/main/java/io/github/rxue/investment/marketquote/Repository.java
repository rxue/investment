package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.marketquote.yahoofinance.QuoteSummaryFetcher;
import io.github.rxue.investment.marketquote.yahoofinance.YahooMetric;
import io.github.rxue.investment.marketquote.yahoofinance.YahooMetricValues;
import io.github.rxue.investment.vo.Price;
import io.github.rxue.investment.vo.PriceQuote;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static io.github.rxue.investment.marketquote.FundamentalMetric.LATEST_PRICE;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;

public class Repository {
    private final QuoteSummaryFetcher quoteSummaryFetcher;

    public Repository() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.quoteSummaryFetcher = new QuoteSummaryFetcher(httpClient);
    }

    public List<MetricValues> getMetrics(Collection<String> yahooTickerSymbols, Collection<Metric> requiredMetrics) {
        final Metrics metrics = new Metrics(requiredMetrics);
        return yahooTickerSymbols.stream()
                .map(s -> new MetricValues(s, getSingleStockMetrics(s, metrics)))
                .toList();
    }

    private Map<Metric,Object> getSingleStockMetrics(String yahooTickerSymbol, Metrics metric) {
        Set<YahooMetric<?>> allNeededYahooMetrics = metric.allNeededYahooMetrics();
        YahooMetricValues allNeededYahooMetricValues = quoteSummaryFetcher.getValues(yahooTickerSymbol, allNeededYahooMetrics);
        Map<Metric,Object> result = new HashMap<>();
        for (FundamentalMetric fundamentalMetric : metric.fundamentalMetrics()) {
            if (fundamentalMetric == LATEST_PRICE) {
                BigDecimal priceValue = allNeededYahooMetricValues.get(REGULAR_MARKET_PRICE);
                String currency = allNeededYahooMetricValues.get(CURRENCY);
                //long epoSeconds = allNeededYahooMetricValues.get(REGULAR_MARKET_TIME);
                //long gmtOffsetInMilliseconds = allNeededYahooMetricValues.get(GMT_OFFSET_IN_MILLISECONDS);
                //ZonedDateTime timestamp = Instant.ofEpochSecond(epoSeconds)
                //    .atZone(ZoneOffset.ofTotalSeconds((int) (gmtOffsetInMilliseconds / 1000)));
                Price price = new Price(priceValue.movePointRight(2).longValue(), currency);
                result.put(LATEST_PRICE, price);
            }
        }
        for (YahooMetric<?> yahooMetric: metric.yahooMetrics()) {
            result.put(yahooMetric, allNeededYahooMetricValues.get(yahooMetric));
        }
        return Collections.unmodifiableMap(result);
    }


}
