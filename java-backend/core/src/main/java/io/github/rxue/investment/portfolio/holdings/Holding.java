package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transactions.Trade;

import static io.github.rxue.investment.portfolio.transactions.Trade.Type.BUY;

public record Holding(String securityId, int position) {
    Holding combine(Trade trade) {
        if (trade.type() == BUY) {
            return new Holding(securityId, position + trade.shareAmount());
        } else {
            return new Holding(securityId, position - trade.shareAmount());
        }
    }
}
