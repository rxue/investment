package io.github.rxue.investment.adapter;

import io.github.rxue.investment.vo.bankaccount.transaction.TransactionList;


public interface TransactionLoader {
    TransactionList load();
}
