package io.github.rxue.investment.financialstatement;
import io.github.rxue.investment.lotsmatching.TradeMatchResult;
import io.github.rxue.investment.lotsmatching.TradeMatcher;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;
import java.util.List;

import static io.github.rxue.investment.financialstatement.Util.*;

public class IncomeStatement {
    private final Period period;
    private final TransactionList transactionList;
    private final TradeMatchResult tradeMatchResult;
    public IncomeStatement(Period period, TransactionList transactionList) {
        this(period, transactionList.by(period.endDate()), getTradeMatchResult(period, transactionList));
    }

    private static TradeMatchResult getTradeMatchResult(Period period, TransactionList transactionList) {
        return new TradeMatcher().matchInFIFO(transactionList.by(period.endDate()).trades());
    }

    public IncomeStatement(Period period, TransactionList transactionList, TradeMatchResult tradeMatchResult) {
        this.period = period;
        this.transactionList = transactionList.by(period.endDate());
        this.tradeMatchResult = tradeMatchResult;
    }


    public Period period() {
        return period;
    }

    private long realizedGainCents() {
        return tradeMatchResult.sellMatches()
                .since(period.startDate())
                .totalGainInCent();
    }
    private long expensesInCent() {
        List<Expense> expenses = transactionList.since(period.startDate()).expenses();
        return sumCents(expenses);
    }
    public BigDecimal totalExpenses() {
        return toBaseUnit(expensesInCent());
    }
    public FinancialIncome financialIncome() {
        List<Dividend> dividends = transactionList.since(period.startDate()).dividends();
        long dividendIncomeInCent = sumCents(dividends);
        return new FinancialIncome(dividendIncomeInCent, realizedGainCents());
    }
    public BigDecimal totalProfit() {
        FinancialIncome financialIncome = financialIncome();
        return toBaseUnit(financialIncome.totalInCent() + expensesInCent());
    }
    public record FinancialIncome(long dividendInCent, long realizedGainsInCent) {
        public BigDecimal dividend() {
            return toBaseUnit(dividendInCent);
        }
        public BigDecimal capitalGains() {
            return toBaseUnit(realizedGainsInCent);
        }
        private long totalInCent() {
            return dividendInCent + realizedGainsInCent;
        }
        private BigDecimal total() {
            return toBaseUnit(totalInCent());
        }
    }

}
