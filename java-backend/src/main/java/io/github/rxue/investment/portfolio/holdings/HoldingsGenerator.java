package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Price;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.*;

public class HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final MarketQuoteFetcher marketQuoteFetcher;
    public HoldingsGenerator(TradeLotsMatcher tradeLotsMatcher, MarketQuoteFetcher marketQuoteFetcher) {
        this.tradeLotsMatcher = tradeLotsMatcher;
        this.marketQuoteFetcher = marketQuoteFetcher;
    }

    /**
     * corner case: field, percentage of portfolio, has dependency on the total market value of the whole holdings, thus needs to calculated after all other fields are calculated
     *
     * @param trades
     * @param fields
     * @return
     */
    public List<Holding> generate(List<Trade> trades, Fields fields) {
        Map<String,List<Lot.Buy>> unrealizedLotsMap = tradeLotsMatcher.matchInFifo(trades, Map.of())
                .getUnrealizedLotsMap();
        List<OptionalField> commonOptionalFields = fields.commonOptionalFields();
        HoldingBuilderDirector holdingBuilderDirector = new HoldingBuilderDirector(commonOptionalFields, null, marketQuoteFetcher);
        List<Holding.Builder> holdingBuilders = unrealizedLotsMap.entrySet().stream()
                .map(holdingBuilderDirector::direct)
                .toList();
        Field sortBy = fields.sortBy();
        Comparator<Holding> comparator = sortBy == null
                ? (a, b) -> 0
                : Comparator.comparing(holding -> sortKey(holding, sortBy), Comparator.nullsLast(Comparator.naturalOrder()));
        return holdingBuilders.stream()
                .map(Holding.Builder::build)
                .sorted(comparator)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static Comparable<Object> sortKey(Holding holding, Field field) {
        Object value = holding.value(field);
        if (value instanceof Price price) {
            value = price.value();
        }
        return (Comparable<Object>) value;
    }

 }
