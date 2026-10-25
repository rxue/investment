package io.github.rxue.investment.financialstatement;

import io.github.rxue.investmentutility.lotsmatching.SellMatches;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Dividend;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Expense;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Period;

import java.math.BigDecimal;
import java.util.List;

import static io.github.rxue.investment.Util.toBaseUnit;
import static io.github.rxue.investment.financialstatement.Util.*;

public record IncomeStatement(Period period, List<Dividend> dividends, List<Expense> expenses, SellMatches sellMatches) {

    public Period period() {
        return period;
    }
    private long realizedGainCents() {
        return sellMatches.totalGainInCent();
    }
    private long expensesInCent() {
        return sumCents(expenses);
    }
    public BigDecimal totalExpenses() {
        return toBaseUnit(expensesInCent());
    }
    public FinancialIncome financialIncome() {
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
