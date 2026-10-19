package io.github.rxue.investment.financialstatement;

import io.github.rxue.investment.lotsmatching.SellMatch;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.github.rxue.investment.financialstatement.Util.*;

public class IncomeStatement {
    private final Period period;
    private final TradeMatcher tradeMatcher;
    private final TransactionList transactionList;
    public IncomeStatement(Period period, TransactionList transactionList) {
        this.period = period;
        this.tradeMatcher = new TradeMatcher();
        this.transactionList = transactionList.transactionsBy(period.endDate());
    }

    public Period period() {
        return period;
    }

    private long dividendIncomeCents() {
        return sumCents(transactionList.dividendsSince(period.startDate()));
    }
    private long realizedGainCents() {
        List<Trade> trades = transactionList.trades();
        Map<String,List<SellMatch>> realizedGains = tradeMatcher.matchInFIFO(trades)
                .sellMatchesSince(period.startDate());
        return realizedGains.values().stream()
                .flatMap(List::stream)
                .mapToLong(SellMatch::gainInCent)
                .sum();
    }
    private long expensesInCent() {
        return sumCents(transactionList.expensesSince(period.endDate()));
    }
    public BigDecimal totalExpenses() {
        return toBaseUnit(expensesInCent());
    }
    public FinancialIncome financialIncome() {
        return new FinancialIncome(dividendIncomeCents(), realizedGainCents());
    }
    public BigDecimal totalProfit() {
        FinancialIncome financialIncome = financialIncome();
        return toBaseUnit(financialIncome.sum() - expensesInCent());
    }
    public record FinancialIncome(long dividendInCent, long realizedGainsInCent) {
        public BigDecimal dividend() {
            return toBaseUnit(dividendInCent);
        }
        public BigDecimal gains() {
            return toBaseUnit(realizedGainsInCent);
        }
        public long sum() {
            return dividendInCent + realizedGainsInCent;
        }
    }

}
