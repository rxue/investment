package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.FormattedNumber;
import io.github.rxue.investment.vo.Metric;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static io.github.rxue.investment.marketquote.DerivedMetric.LATEST_PRICE;
import static io.github.rxue.investment.marketquote.DerivedMetric.LATEST_PRICE_IN_REPORT_CURRENCY;
import static io.github.rxue.investment.marketquote.QuoteMetric.*;

public abstract class AbstractRepository implements Repository {
    private final FxRateFetcher fxRateFetcher;
    private final String reportCurrency;
    protected AbstractRepository(FxRateFetcher fxRateFetcher, String reportCurrency) {
        this.fxRateFetcher = fxRateFetcher;
        this.reportCurrency = reportCurrency;
    }

    @Override
    public final List<MetricValues> getMetrics(Collection<String> securityIds, Collection<Metric> requiredMetrics) {
        List<MetricValues> metricValuesList = new ArrayList<>();
        for (String securityId : securityIds) {
            Map<Metric,Comparable<?>> metricValues = getSingleStockMetrics(securityId, new Metrics(requiredMetrics));
            metricValuesList.add(new MetricValues(securityId, metricValues));
        }
        return Collections.unmodifiableList(metricValuesList);
    }

    private Map<Metric,Comparable<?>> getSingleStockMetrics(String securityId, Metrics metric) {
        Set<QuoteMetric> allNeededQuoteMetrics = metric.allNeededQuoteMetrics();
        MetricValues quoteMetricValues = getQuoteMetrics(securityId, allNeededQuoteMetrics);
        Map<Metric,Comparable<?>> result = new HashMap<>();
        for (DerivedMetric derivedMetric : metric.derivedMetrics()) {
            if (derivedMetric == LATEST_PRICE) {
                BigDecimal priceValue = quoteMetricValues.get(REGULAR_MARKET_PRICE, FormattedNumber.class)
                        .number();
                String currency = quoteMetricValues.get(CURRENCY, String.class);
                Price price = getLatestPrice(priceValue, currency);
                result.put(LATEST_PRICE, price);
            } else if (derivedMetric == LATEST_PRICE_IN_REPORT_CURRENCY) {
                BigDecimal priceValue = quoteMetricValues.get(REGULAR_MARKET_PRICE, FormattedNumber.class)
                        .number();
                String currency = quoteMetricValues.get(CURRENCY, String.class);
                result.put(LATEST_PRICE_IN_REPORT_CURRENCY, getLatestPriceInReportCurrency(priceValue,currency));
            }
        }
        for (QuoteMetric quoteMetric : metric.quoteMetrics()) {
            result.put(quoteMetric, quoteMetricValues.get(quoteMetric));
        }
        return Collections.unmodifiableMap(result);
    }
    private static Price getLatestPrice(BigDecimal priceValue, String currency) {
        //long epoSeconds = allNeededYahooMetricValues.get(REGULAR_MARKET_TIME);
        //long gmtOffsetInMilliseconds = allNeededYahooMetricValues.get(GMT_OFFSET_IN_MILLISECONDS);
        //ZonedDateTime timestamp = Instant.ofEpochSecond(epoSeconds)
        //    .atZone(ZoneOffset.ofTotalSeconds((int) (gmtOffsetInMilliseconds / 1000)));
        return new Price(priceValue.movePointRight(2).longValue(), currency);
    }
    private BigDecimal getLatestPriceInReportCurrency(BigDecimal priceValue, String currency) {
        if (currency.equals(reportCurrency)) {
            return toMoneyValue(priceValue);
        }
        Map.Entry<LocalDate,BigDecimal> originalCurrencyFxRateFromEuro = fxRateFetcher.getFxRateFromEuro(currency, LocalDate.now());
        Map.Entry<LocalDate,BigDecimal> currencyFxRateFromEuro = fxRateFetcher.getFxRateFromEuro(reportCurrency, LocalDate.now());
        BigDecimal fxRate = originalCurrencyFxRateFromEuro.getValue().divide(currencyFxRateFromEuro.getValue(), MathContext.DECIMAL64);
        return toMoneyValue(priceValue.divide(fxRate, MathContext.DECIMAL64));
    }
    protected abstract MetricValues getQuoteMetrics(String securityId, Set<QuoteMetric> quoteMetrics);
    private static BigDecimal toMoneyValue(BigDecimal priceValue) {
        return priceValue.setScale(2, RoundingMode.HALF_UP);
    }

}
