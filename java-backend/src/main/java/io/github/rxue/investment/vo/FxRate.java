package io.github.rxue.investment.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FxRate(BigDecimal value, LocalDate date) {
}
