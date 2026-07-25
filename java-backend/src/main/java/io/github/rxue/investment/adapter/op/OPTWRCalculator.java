package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.transaction.Transaction;
import io.github.rxue.investment.portfolio.twr.TWRCalculator;
import io.github.rxue.investment.portfolio.twr.output.TWRResult;

import java.io.InputStream;
import java.util.List;

public class OPTWRCalculator {
    private final TWRCalculator calculator;
    private final OPTransactionExtractor opTransactionExtractor;
    public OPTWRCalculator(TWRCalculator calculator, OPTransactionExtractor opTransactionExtractor) {
        this.calculator = calculator;
        this.opTransactionExtractor = opTransactionExtractor;
    }

    public TWRResult calculate(List<InputStream> csvInputStreams) {
        List<OPTransaction> opTransactions = opTransactionExtractor.extract(csvInputStreams);
        List<Transaction> transactions = opTransactions.stream()
                .map(OPTransaction::toTransaction)
                .toList();
        return calculator.calculate(transactions);
    }
}
