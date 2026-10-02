package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transactions.Trade;

import java.util.AbstractMap;
import java.util.Map;

import static io.github.rxue.investment.portfolio.transactions.Trade.Type.BUY;

public record Holding(String securityId, int position) {
    Map.Entry<String,Holding> toMapEntry() {
        return new AbstractMap.SimpleImmutableEntry<>(securityId, this);
    }
    Holding combine(Trade trade) {
        if (trade.type() == BUY) {
            return new Holding(securityId, position + trade.shareAmount());
        } else {
            int remainingPosition = position - trade.shareAmount();
            return remainingPosition == 0 ? null : new Holding(securityId, remainingPosition);
        }
    }
}
