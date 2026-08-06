package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
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
        this(new OPTransactionExtractor(), new HoldingsFieldsGenerator(new TradeLotsMatcher(), new MarketQuoteFetcher(), null));
    }

    public List<Holding> generate(List<InputStream> csvPaths, Set<String> optionalFieldNames, String sortByField) {
        List<Trade> trades = opTransactionExtractor.extract(csvPaths).stream()
                .map(OPTransaction::toTransaction)
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        return holdingsGenerator.generate(trades, getFields(optionalFieldNames, sortByField));
    }
    private static Fields getFields(Set<String> optionalFieldNames, String sortByField) {
        List<OptionalField> optionalFiels = optionalFieldNames.stream()
                .map(OptionalField::valueOf)
                .toList();
        return new Fields(optionalFiels, sortByField == null ? null : OptionalField.valueOf(sortByField));
    }
}