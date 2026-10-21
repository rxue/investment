package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investment.lotsmatching.SellMatches;
import io.github.rxue.investment.lotsmatching.TradeMatchResult;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.github.rxue.investment.financialstatement.Util.toBaseUnit;

public class BalanceSheet {
    private final LocalDate date;
    private final TransactionList transactionList;
    private final TradeMatchResult tradeMatchResult;
    public BalanceSheet(LocalDate date, TransactionList transactionList) {
        this.date = date;
        this.transactionList = transactionList.transactionsBy(date);
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
        return sellMatchesBeforeCurrentYear.totalGainInCent()
            + sumInCentBefore(transactionList.dividends(), startDate)
            + sumInCentBefore(transactionList.expenses(), startDate);
    }
    private LocalDate startDate() {
        return LocalDate.of(date.getYear(), 1, 1);
    }
    public long profitInCentForTheYear() {
        LocalDate startDate = startDate();
        SellMatches sellMatches = tradeMatchResult
                .sellMatches()
                .since(startDate);
        long dividendsInCent = sumInCentSince(transactionList.dividends(), startDate());
        long expensesInCent = sumInCentSince(transactionList.expenses(), startDate());
        return sellMatches.totalGainInCent() + dividendsInCent + expensesInCent;
    }
    private static long sumInCentSince(List<? extends Transaction> transactions, LocalDate inclusiveStartDate) {
        return transactions.stream()
                .filter(tr -> !tr.date().isBefore(inclusiveStartDate))
                .mapToLong(Transaction::cents)
                .sum();
    }
    private static long sumInCentBefore(List<? extends Transaction> transactions, LocalDate date) {
        return transactions.stream()
                .filter(tr -> tr.date().isBefore(date))
                .mapToLong(Transaction::cents)
                .sum();
    }
}
