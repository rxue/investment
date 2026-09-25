package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.holdings.transactions.Trade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HoldingTest {
    @Test
    public void combine_buy() {
        Holding holding = new Holding("PFE", 10);
        Trade trade = new Trade("PFE", 5, Trade.Type.BUY, 16000);
        assertEquals(new Holding("PFE", 15), holding.combine(trade));
    }
    @Test
    public void combine_sell() {
        Holding holding = new Holding("PFE", 10);
        Trade sellTrade = new Trade("PFE", 5, Trade.Type.SELL, 16000);
        assertEquals(new Holding("PFE", 5), holding.combine(sellTrade));
    }
}
