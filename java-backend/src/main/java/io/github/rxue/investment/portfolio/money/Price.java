package io.github.rxue.investment.portfolio.money;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record Price(String currency, BigDecimal value, ZonedDateTime timestamp) {

    public long toCent() {
        return Util.toValueInCent(value);
    }
}
