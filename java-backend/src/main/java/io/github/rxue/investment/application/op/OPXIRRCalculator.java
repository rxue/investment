package io.github.rxue.investment.application.op;
import io.github.rxue.investment.portfolio.transaction.Transaction;
import io.github.rxue.investment.portfolio.xirr.XIRRCalculator;
import io.github.rxue.investment.portfolio.xirr.XIRRResult;

import java.util.List;

public class OPXIRRCalculator {

    private final XIRRCalculator calculator;
    private final YahooCompanySymbolRepository companySymbolRepository;

    public OPXIRRCalculator(XIRRCalculator calculator, YahooCompanySymbolRepository companySymbolRepository) {
        this.calculator = calculator;
        this.companySymbolRepository = companySymbolRepository;
    }

    public OPXIRRCalculator() {
        this(new XIRRCalculator(), new YahooCompanySymbolRepository());
    }

    public XIRRResult calculate(List<OPTransaction> opTransactions) {
        List<Transaction> transactions = opTransactions.stream()
                .map(OPTransaction::toTransaction)
                .toList();
        return calculator.calculate(transactions);
    }
}
