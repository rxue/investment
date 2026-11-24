package io.github.rxue.investment.domain.benchmark;

import java.util.List;

public record BenchmarkComparison(Returns benchmark, List<Returns> compared) {
}
