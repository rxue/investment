package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.vo.bankaccount.transaction.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static io.github.rxue.investment.vo.bankaccount.transaction.Action.BUY;
import static io.github.rxue.investment.vo.bankaccount.transaction.Action.SELL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HoldingsBuilderTest {
    @Test
    public void apply_existing() {
        HoldingsBuilder builder = new HoldingsBuilder()
                .apply(new Trade(LocalDate.of(2025,5,1), BUY, "PFE", 10, 16000));
        builder.apply(new Trade(LocalDate.of(2025, 6, 1), SELL, "PFE", 4, 17000));
        assertEquals(List.of(new Holding("PFE", 6)), List.copyOf(builder.build()));
    }
    @Test
    public void apply_sell_all() {
        HoldingsBuilder builder = new HoldingsBuilder()
                .apply(new Trade(LocalDate.of(2025,5,1), BUY, "PFE", 10, 16000));
        builder.apply(new Trade(LocalDate.of(2025, 6, 1), SELL, "PFE", 10, 17000));
        assertTrue(builder.build().isEmpty());
    }
}
