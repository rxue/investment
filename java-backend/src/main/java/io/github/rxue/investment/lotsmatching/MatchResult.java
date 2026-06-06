package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record MatchResult(Unrealized unrealized, Realized realized) {
    record RealizedLotsGroup(Lot.Sell sellLot, List<Lot.Buy> buyLots) {}
    record Unrealized(List<Lot.Buy> lots) {}
    record Realized(List<RealizedLotsGroup> realizedLotsGroups) {}
}
