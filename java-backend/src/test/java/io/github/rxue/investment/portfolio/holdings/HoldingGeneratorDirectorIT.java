package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HoldingGeneratorDirectorIT {
    private final LocalDate date = LocalDate.of(2026, 6, 8);

    @Test
    void direct_to_set_market_value_as_rounded() {
        List<Lot.Buy> unrealizedLots = List.of(new Lot.Buy(LocalDate.of(2025, 8, 27), 30, 62570L));
        HoldingBuilderDirector out1 = new HoldingBuilderDirector(List.of(OptionalField.REPORT_MARKET_VALUE), date, new MarketQuoteFetcher());
        Holding holdingOnOneDate = out1.direct(Map.entry("PFE", unrealizedLots)).build();
        assertEquals(BigDecimal.valueOf(666.03), holdingOnOneDate.<BigDecimal>value(OptionalField.REPORT_MARKET_VALUE));
    }
}
