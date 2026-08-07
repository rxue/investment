package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.snapshot.PortfolioSnapshot;
import io.github.rxue.investment.vo.Util;
import io.github.rxue.investment.portfolio.holdings.*;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Deposit;
import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.transaction.Transaction;
import org.decampo.xirr.Xirr;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XIRRCalculator {
    private final HoldingsGenerator newHoldingsGenerator;

    private XIRRCalculator(HoldingsGenerator holdingsGenerator) {
        this.newHoldingsGenerator = holdingsGenerator;
    }
    public XIRRCalculator() {
        this(new HoldingsFieldsGenerator(new TradeLotsMatcher(), new MarketQuoteFetcher(), null));
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
        result.add(toCashFlowInput(transactions));
        return result;
    }

    private CashFlowInput toCashFlowInput(List<Transaction> transactions) {
        List<Trade> trades = transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        List<Holding> holdings = newHoldingsGenerator.generate(trades, new Fields(List.of(OptionalField.REPORT_MARKET_VALUE),null));
        BigDecimal remainingCash = transactions.stream()
                .map(Transaction::moneyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PortfolioSnapshot portfolioSnapshot = new PortfolioSnapshot(LocalDate.now(), remainingCash, holdings);
        return new CashFlowInput(portfolioSnapshot.date(), CashFlowType.ASSUMED_LIQUIDATION, 0-portfolioSnapshot.valueInEuroCent());
    }
}
