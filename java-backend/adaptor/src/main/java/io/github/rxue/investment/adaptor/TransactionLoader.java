package io.github.rxue.investment.adaptor;

import io.github.rxue.investment.portfolio.transactions.Transaction;

import java.util.List;

public interface TransactionLoader {
    List<Transaction> load();

}
