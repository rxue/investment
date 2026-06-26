package io.github.rxue.investment.application;

import java.math.BigDecimal;
import java.time.LocalDate;

record OPTransaction(LocalDate effectiveDate,
                    BigDecimal amountInEuro,
                    int category,
                    String explanation,
                    String message) {
}
