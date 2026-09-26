package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transactions.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoldingTest {
    @Test
    public void combine_buy() {
        Holding holding = new Holding("PFE", 10);
        Trade trade = new Trade("PFE", LocalDate.of(2025,5,1), 5, Trade.Type.BUY, 16000);
        assertEquals(new Holding("PFE", 15), holding.combine(trade));
    }
    @Test
    public void combine_sell() {
        Holding holding = new Holding("PFE", 10);
        Trade sellTrade = new Trade("PFE", LocalDate.of(2025,5,1),5, Trade.Type.SELL, 16000);
        assertEquals(new Holding("PFE", 5), holding.combine(sellTrade));
    }
}
