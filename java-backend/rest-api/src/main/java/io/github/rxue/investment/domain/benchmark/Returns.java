package io.github.rxue.investment.domain.benchmark;

import java.util.List;

public record Returns(String id, List<DailyReturn> valueSeries) {
}
