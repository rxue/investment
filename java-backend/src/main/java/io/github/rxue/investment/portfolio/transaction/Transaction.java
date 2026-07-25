package io.github.rxue.investment.portfolio.transaction;

import io.github.rxue.investment.portfolio.money.Util;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Transaction {
    LocalDate date();
    BigDecimal moneyAmount();
    default long moneyInCent() {
        return Util.toValueInCent(moneyAmount());
    }

}
