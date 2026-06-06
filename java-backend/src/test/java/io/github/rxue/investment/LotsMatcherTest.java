package io.github.rxue.investment;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LotsMatcherTest {

    @Test
    void matchInFifo_one_lot_removed() {
        var buy1 = new LotsMatcher.Lot(LocalDate.of(2026, 1, 13), 20, 57596, LotsMatcher.Action.BUY);
        var buy2 = new LotsMatcher.Lot(LocalDate.of(2026, 1, 13), 20, 57468, LotsMatcher.Action.BUY);
        var sell = new LotsMatcher.Lot(LocalDate.of(2026, 1, 14), 20, 61200, LotsMatcher.Action.SELL);

        var result = new LotsMatcher().matchInFifo(List.of(buy1, buy2, sell));

        assertEquals(List.of(buy2), result.unrealized());
    }
}
