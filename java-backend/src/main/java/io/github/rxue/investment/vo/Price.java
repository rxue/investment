package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record Price(long centValue, String currency) {
    @Override
    public String toString() {
        return BigDecimal.valueOf(centValue).divide(BigDecimal.valueOf(100)) + " " + currency;
    }
}
