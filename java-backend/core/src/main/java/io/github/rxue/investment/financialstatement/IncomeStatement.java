package io.github.rxue.investment.financialstatement;

import io.github.rxue.investment.vo.bankaccount.transaction.Dividend;
import io.github.rxue.investment.vo.bankaccount.transaction.Expense;
import io.github.rxue.investment.vo.bankaccount.transaction.Period;
import io.github.rxue.investment.vo.bankaccount.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

import static io.github.rxue.investment.financialstatement.Util.*;

public class IncomeStatement {
    private final Period period;
    private final List<Transaction> transactions;

    public IncomeStatement(Period period, List<Transaction> transactions) {
        this.period = period;
        this.transactions = transactionsWithin(period.endDate(), transactions);
    }

    public Period period() {
        return period;
    }

    private long dividendIncomeCents() {
        List<Dividend> dividends = transactions.stream()
                .filter(tr -> period.includes(tr.date()))
                .filter(Dividend.class::isInstance)
                .map(Dividend.class::cast)
                .toList();
        return sumCents(dividends);
    }

    public BigDecimal dividendIncome() {
        return toBaseUnit(dividendIncomeCents());
    }

    private long totalExpensesCents() {
        List<Expense> expenses = transactions.stream()
                .filter(tr -> period.includes(tr.date()))
                .filter(Expense.class::isInstance)
                .map(Expense.class::cast)
                .toList();
        return sumCents(expenses);
    }
    public BigDecimal totalExpenses() {
        return toBaseUnit(totalExpensesCents());
    }
    public BigDecimal profit() {
        return toBaseUnit(dividendIncomeCents() + totalExpensesCents());
    }
}
