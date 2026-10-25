package io.github.rxue.investment;

import java.math.BigDecimal;

public class Util {
    public static BigDecimal toBaseUnit(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }
}
