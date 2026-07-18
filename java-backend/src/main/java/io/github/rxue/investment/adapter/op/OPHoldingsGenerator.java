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

    public List<Holding> generate(List<InputStream> csvPaths, Set<String> optionalFieldNames) {
        List<Trade> trades = opTransactionExtractor.extract(csvPaths).stream()
                .map(OPTransaction::toTransaction)
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        return holdingsGenerator.generate(trades, getAllOptionalFields(optionalFieldNames));
    }
    private static OptionalField[] getAllOptionalFields(Set<String> optionalFieldNames) {
        return optionalFieldNames.stream()
                .map(OptionalField::valueOf)
                .toArray(OptionalField[]::new);
    }
}