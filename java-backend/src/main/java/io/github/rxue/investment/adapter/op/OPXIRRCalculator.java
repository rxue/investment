package io.github.rxue.investment.adapter.op;
import io.github.rxue.investment.portfolio.transaction.Transaction;
import io.github.rxue.investment.portfolio.xirr.XIRRCalculator;
import io.github.rxue.investment.portfolio.xirr.XIRRResult;

import java.io.InputStream;
import java.util.List;

public class OPXIRRCalculator {

    private final XIRRCalculator calculator;
    private final OPTransactionExtractor opTransactionExtractor;
    public OPXIRRCalculator(XIRRCalculator calculator) {
        this.calculator = calculator;
        this.opTransactionExtractor = new OPTransactionExtractor();
    }

    public OPXIRRCalculator() {
        this(new XIRRCalculator());
    }

    public XIRRResult calculate(List<InputStream> csvInputStreams) {
        List<OPTransaction> opTransactions = opTransactionExtractor.extract(csvInputStreams);
        List<Transaction> transactions = opTransactions.stream()
                .map(OPTransaction::toTransaction)
                .toList();
        return calculator.calculate(transactions);
    }
}
