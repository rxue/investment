package io.github.rxue.investment;

import java.time.LocalDate;

public record OPTransaction(LocalDate effectiveDate,
                            double amountInEuro,
                            int category,
                            String explanation,
                            String message) {
}
