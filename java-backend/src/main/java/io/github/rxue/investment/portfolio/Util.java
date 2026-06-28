package io.github.rxue.investment.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {
    public static long toValueInCent(BigDecimal amount) {
        return amount.abs().movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
