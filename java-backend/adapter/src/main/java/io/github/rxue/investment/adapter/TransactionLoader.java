package io.github.rxue.investment.adapter;

import io.github.rxue.investment.portfolio.transactions.Transaction;

import java.util.List;

public interface TransactionLoader {
    List<Transaction> load();

}
