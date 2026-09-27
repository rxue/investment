package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transactions.Trade;

import static io.github.rxue.investment.portfolio.transactions.Trade.Type.BUY;

public record Holding(String securityId, int position) {
    Holding combine(Trade trade) {
        if (trade.type() == BUY) {
            return new Holding(securityId, position + trade.shareAmount());
        } else {
            int remainingPosition = position - trade.shareAmount();
            return remainingPosition == 0 ? null : new Holding(securityId, remainingPosition);
        }
    }
}
