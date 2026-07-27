package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.MatchResult;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TradeLotsMatchResultTest {

    @Test
    void unrealizedLotsMap_keeps_security_with_empty_unrealized_lots_but_non_empty_realized_lots() {
        Lot.Sell sellLot = new Lot.Sell(LocalDate.of(2025, 9, 3), 5, 10000L);
        Lot.Buy matchedBuyLot = new Lot.Buy(LocalDate.of(2025, 8, 1), 5, 8000L);
        MatchResult.RealizedLotsGroup realizedLotsGroup = new MatchResult.RealizedLotsGroup(sellLot, List.of(matchedBuyLot));

        TradeLotsMatchResult.Generator generator = new TradeLotsMatchResult.Generator();
        generator.add("AAPL", new MatchResult(List.of(realizedLotsGroup), List.of()));

        TradeLotsMatchResult result = generator.generate();

        assertEquals(Map.of(), result.getUnrealizedLotsMap(), "entry with empty unrealized lots should be excluded");
    }
}
