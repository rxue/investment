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
    private TradeLotsMatchResult(Generator generator) {
        this.securityToMatchResult = Collections.unmodifiableMap(generator.securityToMatchResult);
    }
    public Map<String,List<Lot.Buy>> unrealizedLotsMap() {
        return securityToMatchResult.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().unrealizedLots()));
    }

    public static class Generator {
        private final Map<String, MatchResult> securityToMatchResult = new HashMap<>();
        public Generator add(String securityId, MatchResult matchResult) {
            securityToMatchResult.put(securityId, matchResult);
            return this;
        }
        public Generator add(Map.Entry<String,List<Lot.Buy>> existingUnrealizedLots) {
            securityToMatchResult.put(existingUnrealizedLots.getKey(), new MatchResult(List.of(), existingUnrealizedLots.getValue()));
            return this;
        }
        public boolean has(String securityId) {
            return securityToMatchResult.get(securityId) != null;
        }
        public TradeLotsMatchResult generate() {
            return new TradeLotsMatchResult(this);
        }
    }
}
