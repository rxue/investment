package io.github.rxue.investment.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Percentage(BigDecimal value) implements Comparable<Percentage> {
    public String presentedValue() {
        return value.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    @Override
    public int compareTo(Percentage percentage) {
        return value.compareTo(percentage.value);
    }
}
