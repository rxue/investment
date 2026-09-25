package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.holdings.transactions.Trade;

import java.util.*;

import static io.github.rxue.investment.portfolio.holdings.transactions.Trade.Type.BUY;

public record Holding(String tickerSymbol, int position) {
    Holding combine(Trade trade) {
        if (trade.type() == BUY) {
            return new Holding(tickerSymbol, position + trade.shareAmount());
        } else {
            return new Holding(tickerSymbol, position - trade.shareAmount());
        }
    }
/*    static class Builder {
        private Map<String,Holding> holdingsByTickerSymbol;

        public Builder() {
            this.holdingsByTickerSymbol = new HashMap<>();
        }

        public Builder apply(Trade trade) {
            String tickerSymbol = trade.tickerSymbol();
            Holding existingHolding = holdingsByTickerSymbol.get(tickerSymbol);

            return this;
        }
        List<Holding> build() {
            return List.copyOf(holdingsByTickerSymbol.values());
        }
    }*/
}
