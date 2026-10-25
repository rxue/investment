package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investmentutility.lotsmatching.SellMatches;
import io.github.rxue.investmentutility.lotsmatching.TradeMatchResult;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Deposit;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.TransactionList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static io.github.rxue.investment.financialstatement.Util.sumCents;
import static io.github.rxue.investment.Util.toBaseUnit;

public class Equity {
    private final LocalDate date;
    private final TransactionList historicalTransactionList;
    private final TradeMatchResult tradeMatchResult;

    Equity(LocalDate date, TransactionList historicalTransactionList, TradeMatchResult tradeMatchResult) {
        this.date = date;
        this.historicalTransactionList = historicalTransactionList;
        this.tradeMatchResult = tradeMatchResult;
    }

    private long capitalPaidByOwnerInCent() {
        return historicalTransactionList.deposits().stream()
                .mapToLong(Deposit::cents)
                .sum();
    }
    public BigDecimal capitalPaidByOwner() {
        return toBaseUnit(capitalPaidByOwnerInCent());
    }

    private LocalDate startDate() {
        return LocalDate.of(date.getYear(), 1, 1);
    }

    private long profitForTheYearInCent() {
        LocalDate startDate = startDate();
        SellMatches sellMatches = tradeMatchResult
                .sellMatches()
                .since(startDate);
        TransactionList transactionsWithinPeriod = historicalTransactionList.since(startDate);
        long dividendsInCent = sumCents(transactionsWithinPeriod.dividends());
        long expensesInCent = sumCents(transactionsWithinPeriod.expenses());
        return sellMatches.totalGainInCent() + dividendsInCent + expensesInCent;
    }
    public BigDecimal profitForTheYear() {
        return toBaseUnit(profitForTheYearInCent());
    }

    private long retainedEarningsInCent() {
        LocalDate startDate = startDate();
        SellMatches sellMatchesBeforeCurrentYear = tradeMatchResult
                .sellMatches()
                .before(startDate);
        TransactionList transactionsBeforePeriod = historicalTransactionList.by(startDate.minus(1, ChronoUnit.DAYS));
        return sellMatchesBeforeCurrentYear.totalGainInCent()
            + sumCents(transactionsBeforePeriod.dividends())
            + sumCents(transactionsBeforePeriod.expenses());
    }
    public BigDecimal retainedEarnings() {
        return toBaseUnit(retainedEarningsInCent());
    }

    public BigDecimal total() {
        return toBaseUnit(capitalPaidByOwnerInCent() + profitForTheYearInCent() + retainedEarningsInCent());
    }
}
