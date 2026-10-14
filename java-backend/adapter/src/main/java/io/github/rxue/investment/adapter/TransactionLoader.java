package io.github.rxue.investment.adapter;

import io.github.rxue.investment.vo.bankaccount.transaction.Transaction;

import java.util.List;

public interface TransactionLoader {
    List<Transaction> load();

}
