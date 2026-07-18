package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.Price;

import java.math.BigDecimal;

public enum OptionalField implements Field {
    PRICE_IN_EURO(Price.class),
    MARKET_VALUE_IN_EURO(BigDecimal.class),
    PORTFOLIO_WEIGHT(Double.class);

    private final Class<?> type;

    OptionalField(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
