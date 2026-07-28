package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.money.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
        Holding.Builder builder = new Holding.Builder(optionalFields);
        final String securityId = securityIdToUnrealizedLots.getKey();
        builder.set(CompulsoryField.COMPANY_ID, securityId)
                .set(CompulsoryField.POSITION, securityIdToUnrealizedLots.getValue().stream().mapToInt(Lot.Buy::shareAmount).sum());
        if (optionalFields.contains(OptionalField.PRICE)) {
            builder.set(OptionalField.PRICE, doGetPrice(securityId));
        }
        boolean needsPriceInEuro = optionalFields.contains(OptionalField.PRICE_IN_EURO)
                || optionalFields.contains(OptionalField.MARKET_VALUE_IN_EURO);
        if (needsPriceInEuro) {
            priceInEuro = doGetPriceInEuro(securityId);
            if (optionalFields.contains(OptionalField.PRICE_IN_EURO)) {
                builder.set(OptionalField.PRICE_IN_EURO, priceInEuro);
            }
            if (optionalFields.contains(OptionalField.MARKET_VALUE_IN_EURO)) {
                BigDecimal marketValueInEuro = priceInEuro.value()
                        .multiply(BigDecimal.valueOf(builder.<Integer>value(CompulsoryField.POSITION)))
                        .setScale(2, RoundingMode.HALF_UP);
                builder.set(OptionalField.MARKET_VALUE_IN_EURO, marketValueInEuro);
            }
        }
        if (optionalFields.contains(OptionalField.COST)) {
            long cost = securityIdToUnrealizedLots.getValue().stream()
                            .mapToLong(Lot.Buy::valueInCent)
                            .sum();
            builder.set(OptionalField.COST, BigDecimal.valueOf(cost/100));
        }
        return builder;
    }
    private Price doGetPrice(String securityId) {
        return date == null ? marketQuoteFetcher.getCurrentPrice(securityId) : marketQuoteFetcher.getClosePrice(securityId, date);
    }
    private Price doGetPriceInEuro(String securityId) {
        return date == null ? marketQuoteFetcher.getCurrentPriceInEuro(securityId) : marketQuoteFetcher.getClosePriceInEuro(securityId, date);
    }
}
