package io.github.rxue.investment.portfolio.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Transaction {
    LocalDate date();
    BigDecimal moneyAmount();
}
