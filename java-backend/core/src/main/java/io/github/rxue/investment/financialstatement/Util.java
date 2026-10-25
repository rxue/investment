package io.github.rxue.investment.financialstatement;


import io.github.rxue.investmentutility.vo.bankaccount.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;

public class Util {
    private Util() {}
    public static <T extends Transaction> long sumCents(List<T> transactions) {
        return transactions.stream()
                .mapToLong(Transaction::cents)
                .sum();
    }
    public static List<? extends Transaction> before(List<? extends Transaction> transactions, LocalDate exclusiveDate) {
        return transactions.stream()
                .filter(tr -> tr.date().isBefore(exclusiveDate))
                .toList();
    }
    public static List<? extends Transaction> since(List<? extends Transaction> transactions, LocalDate inclusiveStartDate) {
        return transactions.stream()
                .filter(tr -> tr.date().isBefore(inclusiveStartDate))
                .toList();
    }
}
