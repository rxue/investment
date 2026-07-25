package io.github.rxue.investment.portfolio;

import io.github.rxue.investment.portfolio.transaction.Transaction;

import java.util.List;

public interface Calculator<T> {
    T calculate(List<Transaction> transactions);
}
