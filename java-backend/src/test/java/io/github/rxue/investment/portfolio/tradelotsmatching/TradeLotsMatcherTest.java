package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.portfolio.transaction.Action;
import io.github.rxue.investment.portfolio.transaction.Trade;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TradeLotsMatcherTest {
    private final TradeLotsMatcher out = new TradeLotsMatcher();

    @Test
    void matchInFifo_keeps_existing_unrealized_lots_for_a_security_that_has_no_trades() {
        List<Lot.Buy> existingUnrealizedLots = List.of(new Lot.Buy(LocalDate.of(2025, 8, 27), 30, 62570L));
        List<Trade> trades = List.of(
                new Trade(LocalDate.of(2025, 9, 3), BigDecimal.valueOf(-525.66), "STZ", Action.BUY, 4)
        );

        final String existingSecurityId = "PFE";
        TradeLotsMatchResult result = out.matchInFifo(trades, Map.of(existingSecurityId, existingUnrealizedLots));
        Map<String, List<Lot.Buy>> actualLotsMatchResult = result.unrealizedLotsMap();
        assertEquals(2, actualLotsMatchResult.size());
        assertEquals(existingUnrealizedLots, actualLotsMatchResult.get(existingSecurityId), "existing unrealized lots are expected to be in the new match result as well");
        assertNotNull(actualLotsMatchResult.get("STZ"));
    }
}
