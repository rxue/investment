package io.github.rxue.investment.portfolio;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionFilter {
    public List<OPTransaction> findTradings(List<OPTransaction> allTransactions) {
        return allTransactions.stream()
                .filter(transaction -> transaction.category() == 700 && ("NOSTO".equals(transaction.explanation()) || "PANO".equals(transaction.explanation())))
                .toList();
    }
}