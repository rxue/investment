package io.github.rxue.investment.financialstatement;

import io.github.rxue.investment.financialstatement.balancesheet.BalanceSheet;
import io.github.rxue.investmentutility.lotsmatching.TradeMatchResult;
import io.github.rxue.investmentutility.lotsmatching.TradeMatcher;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Period;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.TransactionList;

public record FinancialStatements(BalanceSheet balanceSheet, IncomeStatement incomeStatement, Notes notes) {
    public static FinancialStatements generate(Period period, TransactionList transactionList) {
        final TransactionList transactionListByPeriodEnd = transactionList.by(period.endDate());
        TradeMatchResult tradeMatchResult = new TradeMatcher()
                .matchInFIFO(transactionListByPeriodEnd.trades());
        TransactionList transactionListWithinPeriod = transactionListByPeriodEnd.since(period.startDate());
        IncomeStatement incomeStatement = new IncomeStatement(period,
                transactionListWithinPeriod.dividends(),
                transactionListWithinPeriod.expenses(),
                tradeMatchResult.sellMatches().since(period.startDate()));
        return new FinancialStatements(new BalanceSheet(period.endDate(), transactionListByPeriodEnd), incomeStatement, null);
    }
}

