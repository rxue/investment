package io.github.rxue.investment.portfolio.twr;

import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

record SubPeriod(List<Transaction> transactions, LocalDate endDate) {
    LocalDate startDate() {
        return transactions.get(0).date();
    }
    List<Trade> trades() {
        return transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
    }
    BigDecimal summedCashInEuro() {
        return transactions.stream()
                .map(Transaction::moneyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    int transactionCount() {
        return transactions.size();
    }
    
}
