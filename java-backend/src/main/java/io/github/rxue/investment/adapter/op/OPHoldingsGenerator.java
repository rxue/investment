package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.holdings.*;

import java.io.InputStream;
import java.util.*;

public class OPHoldingsGenerator {
    private final OPTransactionExtractor opTransactionExtractor;
    private final HoldingsGenerator holdingsGenerator;

    private OPHoldingsGenerator(OPTransactionExtractor opTransactionExtractor,
                                HoldingsGenerator holdingsGenerator) {
        this.opTransactionExtractor = opTransactionExtractor;
        this.holdingsGenerator = holdingsGenerator;
    }
    public OPHoldingsGenerator() {
        this(new OPTransactionExtractor(), new HoldingsGenerator());
    }

    public List<Holding> generate(List<InputStream> csvPaths, Set<String> fieldNames) {
        List<Trade> trades = opTransactionExtractor.extract(csvPaths).stream()
                .map(OPTransaction::toTransaction)
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        List<Field> fields = fieldNames.stream().map(Field::valueOf).toList();
        return holdingsGenerator.generate(trades, fields.toArray(Field[]::new));
    }
}