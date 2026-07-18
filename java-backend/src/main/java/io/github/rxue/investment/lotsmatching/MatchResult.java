package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record MatchResult(List<RealizedLotsGroup> realizedLots, List<Lot.Buy> unrealizedLots) {
    public record RealizedLotsGroup(Lot.Sell sellLot, List<Lot.Buy> buyLots) {}
}
