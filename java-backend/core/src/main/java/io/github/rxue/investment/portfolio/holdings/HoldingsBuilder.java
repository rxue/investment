package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transactions.Trade;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HoldingsBuilder {
    private Map<String,Holding> holdingsByTickerSymbol;

    public HoldingsBuilder() {
        this.holdingsByTickerSymbol = new HashMap<>();
    }
    HoldingsBuilder apply(Trade trade) {
        String tickerSymbol = trade.tickerSymbol();
        Holding existing = holdingsByTickerSymbol.get(tickerSymbol);
        if (existing == null) {
            holdingsByTickerSymbol.put(tickerSymbol, new Holding(tickerSymbol, trade.shareAmount()));
        } else {
            Holding combinedHolding = existing.combine(trade);
            if (combinedHolding == null) {
                holdingsByTickerSymbol.remove(tickerSymbol);
            } else {
                holdingsByTickerSymbol.put(tickerSymbol, combinedHolding);
            }
        }
        return this;
    }
    public HoldingsBuilder apply(List<Trade> trades) {
        trades.forEach(this::apply);
        return this;
    }
    public Collection<Holding> build() {
        return holdingsByTickerSymbol.values();
    }
}
