package io.github.rxue.investment.lotsmatching;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.rxue.investment.lotsmatching.MatchResult.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LotsMatcherTest {
    private static LotsMatcher out;
    @BeforeAll
    public static void init() {
        out = new LotsMatcher();
    }

    @Test
    void matchInFifo_one_sell_lot_and_one_lot_dequeued() {
        var buy1 = new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57596);
        var buy2 = new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57468);
        var sell = new Lot.Sell(LocalDate.of(2026, 1, 14), 20, 61200);
        List<RealizedLotsGroup> expectedRealizedLotsGroup = List.of(new RealizedLotsGroup(sell, List.of(buy1)));
        MatchResult expected = new MatchResult(new Unrealized(List.of(buy2)), new Realized(expectedRealizedLotsGroup));
        assertEquals(expected, out.matchInFifo(List.of(buy1, buy2, sell), List.of()));
    }

    @Test
    void matchInFifo_one_sell_lot_and_multiple_lots_dequeued() {
        var buy1 = new Lot.Buy(LocalDate.of(2025, 8, 8), 50, 38900);
        var buy2 = new Lot.Buy(LocalDate.of(2025, 8, 25), 70, 51300);
        var buy3 = new Lot.Buy(LocalDate.of(2025, 11, 26), 90, 58500);
        var sell = new Lot.Sell(LocalDate.of(2025, 5, 7), 140, 187400);
        long lastRemovedLotCostInCent = (58500L * 20) / 90;
        List<RealizedLotsGroup> expectedRealizedLotsGroups = List.of(new RealizedLotsGroup(sell, List.of(buy1, buy2, new Lot.Buy(LocalDate.of(2025, 11, 26), 20, lastRemovedLotCostInCent))));
        MatchResult expected = new MatchResult(new Unrealized(List.of(new Lot.Buy(LocalDate.of(2025, 11, 26), 70, 58500 - lastRemovedLotCostInCent))), new Realized(expectedRealizedLotsGroups));
        assertEquals(expected, out.matchInFifo(List.of(buy1, buy2, buy3, sell), List.of()));
    }
}
