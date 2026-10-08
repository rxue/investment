package io.github.rxue.investment.api;

import io.github.rxue.investment.vo.MetricValues;

import java.util.List;

public record QuoteJob(Status status, List<MetricValues> results, String error) {
    public enum Status { RUNNING, DONE, FAILED }
}
