package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record MatchResult(Realized realized, List<Lot.Buy> unrealizedLots) {
    record RealizedLotsGroup(Lot.Sell sellLot, List<Lot.Buy> buyLots) {}
    record Realized(List<RealizedLotsGroup> realizedLotsGroups) {}
}
