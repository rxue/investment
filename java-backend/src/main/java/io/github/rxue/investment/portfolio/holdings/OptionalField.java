package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.money.Price;

import java.math.BigDecimal;

public enum OptionalField implements Field {
    PRICE(Price.class),
    PRICE_IN_EURO(Price.class),
    MARKET_VALUE_IN_EURO(BigDecimal.class),
    COST(BigDecimal.class),
    TRAILING_PE(BigDecimal.class),
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
