package io.github.rxue.investment.financialstatement.balancesheet;

import java.math.BigDecimal;

public record Assets(BigDecimal listedSharesAtCost, BigDecimal cash) {
    public BigDecimal total() {
        return listedSharesAtCost.add(cash);
    }
}
