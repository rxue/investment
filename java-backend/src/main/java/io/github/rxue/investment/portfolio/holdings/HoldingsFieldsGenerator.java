package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.time.LocalDate;
import java.util.*;

public class HoldingsFieldsGenerator implements HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final HoldingBuildersDirector holdingBuildersDirector;

    public HoldingsFieldsGenerator(TradeLotsMatcher tradeLotsMatcher, MarketQuoteFetcher marketQuoteFetcher, LocalDate date) {
        this.tradeLotsMatcher = tradeLotsMatcher;
        this.holdingBuildersDirector = new HoldingBuildersDirector(marketQuoteFetcher, date);
    }

    @Override
    public List<Holding> generate(List<Trade> trades, Fields fields) {
        Map<String, List<Lot.Buy>> unrealizedLotsMap = tradeLotsMatcher.matchInFifo(trades, Map.of()).getUnrealizedLotsMap();
        return holdingBuildersDirector.direct(unrealizedLotsMap, fields.optionalFields());
    }
}
