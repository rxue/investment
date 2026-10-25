package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

import static io.github.rxue.investment.Util.toBaseUnit;

public class Assets {
    private final List<OpenLots> openLotsList;
    private final List<Transaction> historicalTransactionList;

    Assets(List<OpenLots> openLotsList, List<Transaction> historicalTransactionList) {
        this.openLotsList = openLotsList;
        this.historicalTransactionList = historicalTransactionList;
    }
    private long listedSharesAtCostInCent() {
        return openLotsList.stream()
                .mapToLong(OpenLots::costInCent)
                .sum();
    }
    public BigDecimal listedSharesAtCost() {
        return toBaseUnit(listedSharesAtCostInCent());
    }

    private long remainingCashInCent() {
        long remainingCashInCent = historicalTransactionList.stream()
                .mapToLong(Transaction::cents)
                .sum();
        return remainingCashInCent;
    }

    public BigDecimal cash() {
        long remainingCashInCent = remainingCashInCent();
        return toBaseUnit(remainingCashInCent);
    }

    public BigDecimal total() {
        return toBaseUnit(listedSharesAtCostInCent() + remainingCashInCent());
    }
}
