package io.github.rxue.investment.financialstatement.balancesheet;

import java.math.BigDecimal;

public record Equity(BigDecimal capitalPaidByOwner, BigDecimal profit) {
    public BigDecimal total() {
        return capitalPaidByOwner.add(profit);
    }
}