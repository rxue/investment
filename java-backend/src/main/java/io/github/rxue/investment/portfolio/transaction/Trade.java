package io.github.rxue.investment.portfolio.transaction;

import io.github.rxue.investment.portfolio.Util;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Trade(LocalDate date, BigDecimal moneyAmount, String companyIdentifier, Action action, int shareAmount) implements Transaction {
    public long valueInCent() {
        return Util.toValueInCent(moneyAmount);
    }
}
