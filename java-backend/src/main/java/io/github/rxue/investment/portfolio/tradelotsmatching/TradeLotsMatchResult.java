package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.MatchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeLotsMatchResult {
    private final Map<String,MatchResult> securityToMatchResult;
    private TradeLotsMatchResult(Builder builder) {
        this.securityToMatchResult = Collections.unmodifiableMap(builder.securityToMatchResult);
    }
    public Map<String,List<Lot.Buy>> unrealizedLotsMap() {
        return securityToMatchResult.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().unrealizedLots()));
    }

    public static class Builder {
        private final Map<String, MatchResult> securityToMatchResult = new HashMap<>();
        public Builder add(String securityId, MatchResult matchResult) {
            securityToMatchResult.put(securityId, matchResult);
            return this;
        }
        public TradeLotsMatchResult build() {
            return new TradeLotsMatchResult(this);
        }
    }
}
