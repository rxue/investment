package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.marketquote.Price;

import java.math.BigDecimal;
import java.util.List;

class HoldingBuilderDirector {
    private final List<OptionalField> fields;
    private final EuroPriceFetcher euroPriceFetcher;
    private HoldingBuilderDirector(List<OptionalField> fields, EuroPriceFetcher euroPriceFetcher) {
        this.fields = fields;
        this.euroPriceFetcher = euroPriceFetcher;
    }
    public HoldingBuilderDirector(List<OptionalField> fields) {
        this(fields, new EuroPriceFetcher());
    }

    Holding.Builder direct(String securityId, List<Lot.Buy> unrealizedLots) {
        Holding.Builder builder = new Holding.Builder(fields);
        builder.add(CompulsoryField.COMPANY_ID, securityId)
                .add(CompulsoryField.POSITION, unrealizedLots.stream().mapToInt(Lot.Buy::shareAmount).sum());
        if (fields.contains(OptionalField.PRICE_IN_EURO)) {
            Price priceInEuro = euroPriceFetcher.getCurrentEuroPrice(securityId);
            builder.add(OptionalField.PRICE_IN_EURO, priceInEuro);
        }
        if (fields.contains(OptionalField.MARKET_VALUE_IN_EURO)) {
            Price priceInEuro = euroPriceFetcher.getCurrentEuroPrice(securityId);
            BigDecimal marketValueInEuro = priceInEuro.value().multiply(BigDecimal.valueOf(builder.<Integer>value(CompulsoryField.POSITION)));
            builder.add(OptionalField.MARKET_VALUE_IN_EURO, marketValueInEuro);
        }
        return builder;
    }
}
