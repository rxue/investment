package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HoldingBuilderDirector {
    private final List<OptionalField> optionalFields;
    private final LocalDate date;
    private final MarketQuoteFetcher marketQuoteFetcher;
    private Price priceInEuro;

    public HoldingBuilderDirector(List<OptionalField> optionalFields, LocalDate date, MarketQuoteFetcher marketQuoteFetcher) {
        this.optionalFields = optionalFields;
        this.date = date;
        this.marketQuoteFetcher = marketQuoteFetcher;
    }

    public Holding.Builder direct(Map.Entry<String,List<Lot.Buy>> securityIdToUnrealizedLots) {
        final String securityId = securityIdToUnrealizedLots.getKey();
        Holding.Builder builder = new Holding.Builder(securityId, securityIdToUnrealizedLots.getValue().stream().mapToInt(Lot.Buy::shareAmount).sum());
        if (optionalFields.contains(OptionalField.PRICE)) {
            builder.set(OptionalField.PRICE, doGetPrice(securityId));
        }
        boolean needsPriceInEuro = optionalFields.contains(OptionalField.PRICE_IN_EURO)
                || optionalFields.contains(OptionalField.REPORT_MARKET_VALUE);
        if (needsPriceInEuro) {
            priceInEuro = doGetPriceInEuro(securityId);
            if (optionalFields.contains(OptionalField.PRICE_IN_EURO)) {
                builder.set(OptionalField.PRICE_IN_EURO, priceInEuro);
            }
            if (optionalFields.contains(OptionalField.REPORT_MARKET_VALUE)) {
                BigDecimal marketValueInEuro = priceInEuro.value()
                        .multiply(BigDecimal.valueOf(builder.<Integer>value(CompulsoryField.POSITION)))
                        .setScale(2, RoundingMode.HALF_UP);
                builder.set(OptionalField.REPORT_MARKET_VALUE, marketValueInEuro);
            }
        }
        if (optionalFields.contains(OptionalField.COST)) {
            long cost = securityIdToUnrealizedLots.getValue().stream()
                            .mapToLong(Lot.Buy::valueInCent)
                            .sum();
            builder.set(OptionalField.COST, BigDecimal.valueOf(cost/100));
        }
        setFundamentalMetricsFields(builder, securityId);
        return builder;
    }

    private void setFundamentalMetricsFields(Holding.Builder holdingBuilder, String securityId) {
        Map<String,Object> fetchedMetrics = marketQuoteFetcher.getFundamentals(securityId, getFundamentalMetrics());
        for (OptionalField field : optionalFields) {
            String yahooMetricName = field.yahooMetricName();
            if (yahooMetricName != null) {
                holdingBuilder.set(field, fetchedMetrics.get(yahooMetricName));
            }
        }
    }
    private List<String> getFundamentalMetrics() {
        return optionalFields.stream()
                .map(OptionalField::yahooMetricName)
                .filter(Objects::nonNull)
                .toList();
    }

    private Price doGetPrice(String securityId) {
        return date == null ? marketQuoteFetcher.getCurrentPrice(securityId) : marketQuoteFetcher.getClosePrice(securityId, date);
    }
    private Price doGetPriceInEuro(String securityId) {
        return date == null ? marketQuoteFetcher.getCurrentPriceInEuro(securityId) : marketQuoteFetcher.getClosePriceInEuro(securityId, date);
    }
}
