package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.vo.Price;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LegacyHoldingsGenerator implements HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final MarketQuoteFetcher marketQuoteFetcher;
    public LegacyHoldingsGenerator(TradeLotsMatcher tradeLotsMatcher, MarketQuoteFetcher marketQuoteFetcher) {
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
    @Override
    public List<Holding> generate(List<Trade> trades, Fields fields) {
        Map<String,List<Lot.Buy>> unrealizedLotsMap = tradeLotsMatcher.matchInFifo(trades, Map.of())
                .getUnrealizedLotsMap();
        List<OptionalField> commonOptionalFields = fields.commonOptionalFields();
        HoldingBuilderDirector holdingBuilderDirector = new HoldingBuilderDirector(commonOptionalFields, null, marketQuoteFetcher);
        List<Holding.Builder> holdingBuilders = unrealizedLotsMap.entrySet().stream()
                .map(holdingBuilderDirector::direct)
                .toList();
        postCalculate(holdingBuilders, fields.getPostCalculatedFields());
        Field sortBy = fields.sortBy();
        Comparator<Holding> comparator = sortBy == null
                ? (a, b) -> 0
                : Comparator.comparing(holding -> sortKey(holding, sortBy), Comparator.nullsLast(Comparator.naturalOrder()));
        return holdingBuilders.stream()
                .map(Holding.Builder::build)
                .sorted(comparator)
                .toList();
    }

    private static void postCalculate(List<Holding.Builder> holdingBuilder, List<OptionalField> postCalculatedField) {
        if (postCalculatedField.contains(OptionalField.PORTFOLIO_WEIGHT)) {
            if (holdingBuilder.contains(OptionalField.REPORT_MARKET_VALUE)) {

            } else {

            }
        }
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
