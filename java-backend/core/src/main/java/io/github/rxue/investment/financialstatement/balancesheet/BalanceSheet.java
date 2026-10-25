package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investmentutility.lotsmatching.TradeMatchResult;
import io.github.rxue.investmentutility.lotsmatching.TradeMatcher;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.TransactionList;

import java.time.LocalDate;

public class BalanceSheet {
    private final LocalDate date;
    private final TransactionList historicalTransactionList;
    private final TradeMatchResult tradeMatchResult;
    public BalanceSheet(LocalDate date, TransactionList historicalTransactionList) {
        this.date = date;
        this.historicalTransactionList = historicalTransactionList.by(date);
        this.tradeMatchResult = tradeMatchResult();
    }
    private TradeMatchResult tradeMatchResult() {
        return new TradeMatcher().matchInFIFO(historicalTransactionList.trades());
    }

    public LocalDate date() {
        return date;
    }

    public Assets assets() {
        return new Assets(tradeMatchResult.openLotsList(), historicalTransactionList.transactionList());
    }
    public Equity equity() {
        return new Equity(date, historicalTransactionList, tradeMatchResult);
    }
}
