package io.github.rxue.investment.financialstatement;
import io.github.rxue.investment.vo.bankaccount.transaction.*;

import java.math.BigDecimal;

import static io.github.rxue.investment.financialstatement.Util.*;

public class IncomeStatement {
    private final Period period;
    private final IncomeElements incomeElements;
    public IncomeStatement(Period period, TransactionList transactionList) {
        this(period, new IncomeElements(transactionList));
    }
    public IncomeStatement(Period period, IncomeElements incomeElements) {
        this.period = period;
        this.incomeElements = incomeElements;
    }


    public Period period() {
        return period;
    }

    private long realizedGainCents() {
        return incomeElements.tradeMatchResult()
                .sellMatches()
                .since(period.startDate())
                .totalGainInCent();
    }
    private long expensesInCent() {
        return sumCents(incomeElements.expensesSince(period.startDate()));
    }
    public BigDecimal totalExpenses() {
        return toBaseUnit(expensesInCent());
    }
    public FinancialIncome financialIncome() {
        long dividendIncomeInCent = sumCents(incomeElements.dividendsSince(period.startDate()));
        return new FinancialIncome(dividendIncomeInCent, realizedGainCents());
    }
    public BigDecimal totalProfit() {
        FinancialIncome financialIncome = financialIncome();
        return toBaseUnit(financialIncome.totalInCent() - expensesInCent());
    }
    public record FinancialIncome(long dividendInCent, long realizedGainsInCent) {
        public BigDecimal dividend() {
            return toBaseUnit(dividendInCent);
        }
        public BigDecimal capitalGains() {
            return toBaseUnit(realizedGainsInCent);
        }
        public long totalInCent() {
            return dividendInCent + realizedGainsInCent;
        }
    }

}
