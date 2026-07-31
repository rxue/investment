package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;

public enum OptionalField implements Field {
    PRICE(Price.class, null),
    PRICE_IN_EURO(Price.class, null),
    MARKET_VALUE_IN_EURO(BigDecimal.class, null),
    COST(BigDecimal.class, null),
    TRAILING_PE(BigDecimal.class, "trailingPE"),
    PORTFOLIO_WEIGHT(Double.class, null);

    private final Class<?> type;
    private final String yahooMetricName;

    private OptionalField(Class<?> type, String yahooMetricName) {
        this.type = type;
        this.yahooMetricName = yahooMetricName;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    public String yahooMetricName() {
        return yahooMetricName;
    }
}
