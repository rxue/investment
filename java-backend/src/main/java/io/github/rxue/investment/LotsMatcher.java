package io.github.rxue.investment;

import java.time.LocalDate;
import java.util.List;

public class LotsMatcher {

    enum Action {
        BUY, SELL
    }

    record Lot(LocalDate date, int shareAmount, long valueInCent, Action action) {}

    public record MatchingResult(List<Lot> realized, List<Lot> unrealized) {}

    public MatchingResult matchInFifo(List<Lot> lots) {
        throw new UnsupportedOperationException();
    }
}
