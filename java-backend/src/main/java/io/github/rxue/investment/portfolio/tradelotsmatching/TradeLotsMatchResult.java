package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.MatchResult;


public record TradeLotsMatchResult(String securityId, MatchResult result) {
    public int unrealizedShareAmount() {
        return result.unrealizedLots().stream()
                .mapToInt(Lot.Buy::shareAmount)
                .sum();
    }
}
