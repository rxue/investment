package io.github.rxue.investment.domain.benchmark;

import java.time.LocalDate;

public record DailyReturn(LocalDate time, double value) {
}
