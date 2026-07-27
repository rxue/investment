package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.PriceFetcher;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.*;
import java.util.function.Predicate;

public class HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final PriceFetcher priceFetcher;
    static final Set<Field> POST_CALCULATED_FIELDS = Set.of(OptionalField.PORTFOLIO_WEIGHT);
    private HoldingsGenerator(TradeLotsMatcher tradeLotsMatcher, PriceFetcher priceFetcher) {
        this.tradeLotsMatcher = tradeLotsMatcher;
        this.priceFetcher = priceFetcher;
    }

    public HoldingsGenerator() {
        this(new TradeLotsMatcher(), new PriceFetcher());
    }

    /**
     * corner case: field, percentage of portfolio, has dependency on the total market value of the whole holdings, thus needs to calculated after all other fields are calculated
     *
     * @param trades
     * @param optionalFields
     * @return
     */
    public List<Holding> generate(List<Trade> trades, OptionalField... optionalFields) {
        Map<String,List<Lot.Buy>> unrealizedLotsMap = tradeLotsMatcher.matchInFifo(trades, Map.of())
                .getUnrealizedLotsMap();
        List<OptionalField> commonOptionalFields = Arrays.stream(optionalFields)
                .filter(((Predicate<OptionalField>) POST_CALCULATED_FIELDS::contains).negate())
                .toList();
        HoldingBuilderDirector holdingBuilderDirector = new HoldingBuilderDirector(commonOptionalFields, null, priceFetcher);
        List<Holding.Builder> holdingBuilders = unrealizedLotsMap.entrySet().stream()
                .map(holdingBuilderDirector::direct)
                .toList();
        return holdingBuilders.stream()
                .map(Holding.Builder::build)
                .toList();
    }

 }
