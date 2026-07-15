package io.github.rxue.investment.portfolio.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(LocalDate date, BigDecimal moneyAmount) implements Transaction {
}
