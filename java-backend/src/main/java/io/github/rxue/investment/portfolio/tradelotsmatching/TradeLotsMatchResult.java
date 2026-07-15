package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.MatchResult;

public record TradeLotsMatchResult(String securityId, MatchResult result) {
}
