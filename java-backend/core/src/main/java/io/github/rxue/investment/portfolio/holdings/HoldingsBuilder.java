package io.github.rxue.investment.portfolio.holdings;




import io.github.rxue.investmentutility.lotsmatching.OpenLots;
import io.github.rxue.investmentutility.lotsmatching.TradeMatcher;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


public class HoldingsBuilder {
    private final TradeMatcher tradeMatcher;
    private List<OpenLots> openLotsList;
    public HoldingsBuilder() {
        this.tradeMatcher = new TradeMatcher();
    }
    public HoldingsBuilder apply(List<Trade> trades) {
        openLotsList = tradeMatcher.matchInFIFO(trades).openLotsList();
        return this;
    }
    public Collection<Holding> build() {
        return openLotsList.stream()
                .map(Holding::of)
                .toList();
    }
    public Map<String,Holding> buildMap() {
        return build().stream()
                .collect(toMap(Holding::securityId, Function.identity()));
    }
}
