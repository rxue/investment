package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record FormattedNumber(BigDecimal number, String formattedValue) implements Comparable<FormattedNumber> {
    @Override
    public String toString() {
        return formattedValue;
    }
    @Override
    public int compareTo(FormattedNumber formattedNumber) {
        return number.compareTo(formattedNumber.number);
    }
}
