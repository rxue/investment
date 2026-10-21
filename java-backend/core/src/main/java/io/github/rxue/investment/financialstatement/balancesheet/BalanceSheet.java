package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investment.lotsmatching.SellMatches;
import io.github.rxue.investment.lotsmatching.TradeMatchResult;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static io.github.rxue.investment.financialstatement.Util.sumCents;
import static io.github.rxue.investment.financialstatement.Util.toBaseUnit;

public class BalanceSheet {
    private final LocalDate date;
    private final TransactionList transactionList;
    private final TradeMatchResult tradeMatchResult;
    public BalanceSheet(LocalDate date, TransactionList transactionList) {
        this.date = date;
        this.transactionList = transactionList.by(date);
        this.tradeMatchResult = tradeMatchResult();
    }
    private TradeMatchResult tradeMatchResult() {
        return new TradeMatcher().matchInFIFO(transactionList.trades());
    }

    public LocalDate date() {
        return date;
    }

    public Assets assets() {
        BigDecimal sharesAtCost = toBaseUnit(tradeMatchResult.totalOpenLotsCostInCent());
        long cashInCent = transactionList.transactionList().stream()
                .mapToLong(Transaction::cents)
                .sum();
        return new Assets(sharesAtCost, toBaseUnit(cashInCent));
    }
    public Equity equity() {
        long totalDeposits = transactionList.deposits().stream()
                .mapToLong(Deposit::cents)
                .sum();
        return new Equity(toBaseUnit(totalDeposits), toBaseUnit(profitInCentForTheYear()), toBaseUnit(retainedEarningsInCent()));
    }
    private long retainedEarningsInCent() {
        LocalDate startDate = startDate();
        SellMatches sellMatchesBeforeCurrentYear = tradeMatchResult
                .sellMatches()
                .before(startDate);
        TransactionList transactionsBeforePeriod = transactionList.by(startDate.minus(1, ChronoUnit.DAYS));
        return sellMatchesBeforeCurrentYear.totalGainInCent()
            + sumCents(transactionsBeforePeriod.dividends())
            + sumCents(transactionsBeforePeriod.expenses());
    }
    private LocalDate startDate() {
        return LocalDate.of(date.getYear(), 1, 1);
    }
    public long profitInCentForTheYear() {
        LocalDate startDate = startDate();
        SellMatches sellMatches = tradeMatchResult
                .sellMatches()
                .since(startDate);
        TransactionList transactionsWithinPeriod = transactionList.since(startDate);
        long dividendsInCent = sumCents(transactionsWithinPeriod.dividends());
        long expensesInCent = sumCents(transactionsWithinPeriod.expenses());
        return sellMatches.totalGainInCent() + dividendsInCent + expensesInCent;
    }
}
