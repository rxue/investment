package io.github.rxue.investment.portfolio.transactions;

import java.time.LocalDate;

public interface Transaction {
    LocalDate date();
    long cents();
}
