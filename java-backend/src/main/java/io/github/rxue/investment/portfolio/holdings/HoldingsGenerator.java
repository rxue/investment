package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.List;
import java.util.Map;

public class HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final HoldingGenerator holdingGenerator;
    private HoldingsGenerator(TradeLotsMatcher tradeLotsMatcher, HoldingGenerator holdingGenerator) {
        this.tradeLotsMatcher = tradeLotsMatcher;
        this.holdingGenerator = holdingGenerator;
    }

    public HoldingsGenerator() {
        this(new TradeLotsMatcher(new LotsMatcher()), new HoldingGenerator());
    }

    public List<Holding> generate(List<Trade> trades, Field... optionalFields) {
        return tradeLotsMatcher.matchAllInFifo(trades, Map.of()).stream()
                .map(matchResult -> holdingGenerator.generate(matchResult.securityId(), matchResult.result(), optionalFields))
                .toList();
    }
}
