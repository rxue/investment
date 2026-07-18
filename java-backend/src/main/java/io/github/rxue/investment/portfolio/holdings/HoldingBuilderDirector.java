package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.PriceFetcher;
import io.github.rxue.investment.marketquote.Price;

import java.math.BigDecimal;
import java.util.List;

class HoldingBuilderDirector {
    private final List<OptionalField> optionalFields;
    private final PriceFetcher priceFetcher;

    private HoldingBuilderDirector(List<OptionalField> optionalFields, PriceFetcher priceFetcher) {
        this.optionalFields = optionalFields;
        this.priceFetcher = priceFetcher;
    }
    public HoldingBuilderDirector(List<OptionalField> fields) {
        this(fields, new PriceFetcher());
    }

    Holding.Builder direct(String securityId, List<Lot.Buy> unrealizedLots) {
        Holding.Builder builder = new Holding.Builder(optionalFields);
        builder.add(CompulsoryField.COMPANY_ID, securityId)
                .add(CompulsoryField.POSITION, unrealizedLots.stream().mapToInt(Lot.Buy::shareAmount).sum());
        boolean needsPrice = optionalFields.contains(OptionalField.PRICE_IN_EURO)
                || optionalFields.contains(OptionalField.MARKET_VALUE_IN_EURO);
        if (needsPrice) {
            Price priceInEuro = priceFetcher.getCurrentPriceInEuro(securityId);
            if (optionalFields.contains(OptionalField.PRICE_IN_EURO)) {
                builder.add(OptionalField.PRICE_IN_EURO, priceInEuro);
            }
            if (optionalFields.contains(OptionalField.MARKET_VALUE_IN_EURO)) {
                builder.add(OptionalField.MARKET_VALUE_IN_EURO,
                        priceInEuro.value().multiply(BigDecimal.valueOf(builder.<Integer>value(CompulsoryField.POSITION))));
            }
        }
        return builder;
    }
}
