package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.Util;
import io.github.rxue.investment.portfolio.holdings.Field;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.HoldingsGenerator;
import io.github.rxue.investment.portfolio.transaction.Deposit;
import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.transaction.Transaction;
import org.decampo.xirr.Xirr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class XIRRCalculator {
    private final HoldingsGenerator holdingsGenerator;

    private XIRRCalculator(HoldingsGenerator holdingsGenerator) {
        this.holdingsGenerator = holdingsGenerator;
    }
    public XIRRCalculator() {
        this(new HoldingsGenerator());
    }

    public XIRRResult calculate(List<Transaction> transactions) {
        List<CashFlowInput> cashFlowInputList = getCashFlowInput(transactions);
        List<org.decampo.xirr.Transaction> xirrTransactions = cashFlowInputList.stream()
                .map(cf -> new org.decampo.xirr.Transaction(cf.getValueInCent(), cf.getDate()))
                .toList();
        return new XIRRResult(cashFlowInputList, BigDecimal.valueOf(new Xirr(xirrTransactions).xirr()));
    }

    private List<CashFlowInput> getCashFlowInput(List<Transaction> transactions) {
        List<CashFlowInput> result = new ArrayList<>();
        transactions.stream()
                .filter(Deposit.class::isInstance)
                .map(Deposit.class::cast)
                .map(cf -> new CashFlowInput(cf.date(), CashFlowType.DEPOSIT, Util.toValueInCent(cf.moneyAmount())))
                .forEach(result::add);
        result.add(getAssumedLiquidation(transactions).toCashFlowInput());
        return result;
    }

    private AssumedLiquidation getAssumedLiquidation(List<Transaction> transactions) {
        List<Trade> trades = transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        List<Holding> holdings = holdingsGenerator.generate(trades, Field.EURO_PRICE);
        BigDecimal remainingCash = transactions.stream()
                .map(Transaction::moneyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AssumedLiquidation(holdings, remainingCash);
    }
}
