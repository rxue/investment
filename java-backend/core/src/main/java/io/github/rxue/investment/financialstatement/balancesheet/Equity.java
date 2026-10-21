package io.github.rxue.investment.financialstatement.balancesheet;

import java.math.BigDecimal;

public record Equity(BigDecimal capitalPaidByOwner, BigDecimal profitForTheYear, BigDecimal retainedEarnings) {
    public BigDecimal total() {
        return capitalPaidByOwner
                .add(profitForTheYear)
                .add(retainedEarnings);
    }
}