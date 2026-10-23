package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.BUY;
import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.SELL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoldingTest {
    @Test
    public void combine_buy() {
        Holding holding = new Holding("PFE", 10);
        Trade trade = new Trade(LocalDate.of(2025,5,1), BUY, "PFE", 5, 16000);
        assertEquals(new Holding("PFE", 15), holding.combine(trade));
    }
    @Test
    public void combine_sell() {
        Holding holding = new Holding("PFE", 10);
        Trade sellTrade = new Trade(LocalDate.of(2025,5,1), SELL, "PFE", 5, 16000);
        assertEquals(new Holding("PFE", 5), holding.combine(sellTrade));
    }
}
