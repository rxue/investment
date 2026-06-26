package io.github.rxue.investment.portfolio.holdings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record Trade(LocalDate date, String companyIdentifier, Type type, int shareAmount, BigDecimal value) {
    public enum Type {
        BUY, SELL
    }
    public long valueInCent() {
        return value.abs().movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
