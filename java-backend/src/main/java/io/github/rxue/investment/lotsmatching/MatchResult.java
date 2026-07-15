package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record MatchResult(Unrealized unrealized, Realized realized) {
    record RealizedLotsGroup(Lot.Sell sellLot, List<Lot.Buy> buyLots) {}
    public record Unrealized(List<Lot.Buy> lots) {
        public int shareAmount() {
            return lots.stream()
                    .mapToInt(Lot.Buy::shareAmount)
                    .sum();
        }

        /**
         * utility function primarily used in unit tests
         * @return
         */
        static Unrealized empty() {
            return new Unrealized(List.of());
        }
    }
    record Realized(List<RealizedLotsGroup> realizedLotsGroups) {}

}
