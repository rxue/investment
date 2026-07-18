package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.MatchResult;

import java.util.List;


public record SingleSecurityTradeLotsMatchResult(String securityId, MatchResult result) {
    public int unrealizedShareAmount() {
        return result.unrealizedLots().stream()
                .mapToInt(Lot.Buy::shareAmount)
                .sum();
    }
    public List<Lot.Buy> unrealizedLots() {
        return result.unrealizedLots();
    }
    public boolean hasUnrealizedLots() {
        return !result.unrealizedLots().isEmpty();
    }
}
