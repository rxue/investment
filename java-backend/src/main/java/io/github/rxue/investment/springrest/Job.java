package io.github.rxue.investment.springrest;

import io.github.rxue.investment.portfolio.xirr.XIRRResult;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

record Job(long id, Future<XIRRResult> result) {
    private static AtomicLong idGenerator = new AtomicLong(0);
    public static Job newJob(Future<XIRRResult> result) {
        return new Job(idGenerator.incrementAndGet(), result);
    }
}
