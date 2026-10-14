package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investment.lotsmatching.TradeMatchResult;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.github.rxue.investment.financialstatement.Util.toBaseUnit;
import static io.github.rxue.investment.financialstatement.Util.transactionsWithin;

public class BalanceSheet {
    private final LocalDate date;
    private final List<Transaction> transactionList;
    private final TradeMatchResult tradeMatchResult;
    public BalanceSheet(LocalDate date, List<Transaction> allTransactionList) {
        this.date = date;
        this.transactionList = transactionsWithin(date, allTransactionList);
        this.tradeMatchResult = new TradeMatcher()
                .matchInFIFO(trades(transactionList));
    }

    private static List<Trade> trades(List<Transaction> transactionList) {
        return transactionList.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
    }

    public LocalDate date() {
        return date;
    }

    public Assets assets() {
        BigDecimal sharesAtCost = toBaseUnit(tradeMatchResult.totalOpenLotsCostInCent());
        long cashInCent = transactionList.stream()
                .mapToLong(Transaction::cents)
                .sum();
        return new Assets(sharesAtCost, toBaseUnit(cashInCent));
    }
    public Equity equity() {
        long totalDeposits = transactionList.stream()
                .filter(Deposit.class::isInstance)
                .mapToLong(Transaction::cents)
                .sum();
        long dividends = transactionList.stream()
                .filter(Dividend.class::isInstance)
                .mapToLong(Transaction::cents)
                .sum();
        long expenses = transactionList.stream()
                .filter(Expense.class::isInstance)
                .mapToLong(Transaction::cents)
                .sum();
        return new Equity(toBaseUnit(totalDeposits), toBaseUnit(dividends+expenses));
    }
}
