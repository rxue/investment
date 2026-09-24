package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record Price(long centValue, String currency) implements Comparable<Price> {
    @Override
    public String toString() {
        return BigDecimal.valueOf(centValue).divide(BigDecimal.valueOf(100)) + " " + currency;
    }

    @Override
    public int compareTo(Price price) {
        return Long.compare(centValue, price.centValue);
    }
}
