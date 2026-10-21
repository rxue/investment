package io.github.rxue.investment.financialstatement;

import io.github.rxue.investment.lotsmatching.TradeMatchResult;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.Dividend;
import io.github.rxue.investment.vo.bankaccount.transaction.Expense;
import io.github.rxue.investment.vo.bankaccount.transaction.TransactionList;

import java.time.LocalDate;
import java.util.List;

public class IncomeElements {
    private final TradeMatcher tradeMatcher;
    private final TransactionList transactionList;

    public IncomeElements(TransactionList transactionList) {
        this.tradeMatcher = new TradeMatcher();
        this.transactionList = transactionList;
    }
    public List<Dividend> dividends() {
        return transactionList.dividends();
    }

    public List<Dividend> dividendsSince(LocalDate inclusiveStartDate) {
        return transactionList.dividends().stream()
                .filter(dv -> !dv.date().isBefore(inclusiveStartDate))
                .toList();
    }

    public List<Expense> expenses() {
        return transactionList.expenses();
    }
    public List<Expense> expensesSince(LocalDate inclusiveStartDate) {
        return expenses().stream()
                .filter(exp -> !exp.date().isBefore(inclusiveStartDate))
                .toList();
    }
    public TradeMatchResult tradeMatchResult() {
        return tradeMatcher.matchInFIFO(transactionList.trades());
    }
}
