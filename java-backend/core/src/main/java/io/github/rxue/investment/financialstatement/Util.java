package io.github.rxue.investment.financialstatement;

import io.github.rxue.investment.vo.bankaccount.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Util {
    private Util() {}
    public static <T extends Transaction> long sumCents(List<T> transactions) {
        return transactions.stream()
                .mapToLong(Transaction::cents)
                .sum();
    }
    public static BigDecimal toBaseUnit(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }
    public static List<Transaction> transactionsWithin(LocalDate date, List<Transaction> allTransactions) {
        return allTransactions.stream()
                .filter(tr -> !tr.date().isAfter(date))
                .toList();
    }
}
