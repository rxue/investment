package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.metric.MetricValues;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ParallelRepository<R extends AbstractRepository> implements Repository {
    private final List<R> parallelRepository;
    private final ExecutorService executorService;
    public ParallelRepository(List<R> parallelRepository, ExecutorService executorService) {
        this.parallelRepository = parallelRepository;
        this.executorService = executorService;
    }

    @Override
    public List<MetricValues> getMetricValues(Set<String> securityIds, Collection<QuoteMetric> requiredMetrics) {
        // one batch per repository, security ids dealt out round-robin
        List<Set<String>> batches = createBatches(securityIds);
        List<Future<List<MetricValues>>> futures = new ArrayList<>();
        for (int i = 0; i < batches.size(); i++) {
            final Set<String> batch = batches.get(i);
            if (batch.isEmpty()) continue;
            final Repository currentRepository = parallelRepository.get(i);
            futures.add(executorService.submit(() -> currentRepository.getMetricValues(batch, requiredMetrics)));
        }
        List<MetricValues> metricValuesList = new ArrayList<>();
        try {
            for (Future<List<MetricValues>> future : futures) {
                metricValuesList.addAll(future.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching metric values", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to fetch metric values", e.getCause());
        } finally {
            futures.forEach(future -> future.cancel(true));
        }
        return Collections.unmodifiableList(metricValuesList);
    }

    private List<Set<String>> createBatches(Set<String> securityIds) {
        List<Set<String>> batches = new ArrayList<>();
        for (int i = 0; i < parallelRepository.size(); i++) {
            batches.add(new HashSet<>());
        }
        int batchIndex = 0;
        for (String securityId : securityIds) {
            batches.get(batchIndex).add(securityId);
            batchIndex = (batchIndex + 1) % batches.size();
        }
        return Collections.unmodifiableList(batches);
    }
}
