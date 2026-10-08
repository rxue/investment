package io.github.rxue.investment.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.MetricValues;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.*;

import static io.github.rxue.investment.api.QuoteJob.Status.DONE;
import static io.github.rxue.investment.api.QuoteJob.Status.FAILED;
import static io.github.rxue.investment.api.QuoteJob.Status.RUNNING;

public class JobRepository {
    private final Cache<UUID,QuoteJob> jobs = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(1_000)
            .build();
    private final Repository marketQuoteRepository;

    public JobRepository(Repository marketQuoteRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
    }

    public UUID create() {
        UUID jobId = UUID.randomUUID();
        jobs.put(jobId, new QuoteJob(RUNNING, List.of(), null));
        return jobId;
    }

    @Async
    public void query(UUID jobId, Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<MetricValues> metricValuesResult = new ArrayList<>();
        try {
            for (String securityId : securityIds) {
                Map<QuoteMetric,Comparable<?>> metricValues = marketQuoteRepository.findMetricValues(securityId, quoteMetrics);
                metricValuesResult.add(new MetricValues(securityId, Collections.unmodifiableMap(metricValues)));
                jobs.put(jobId, new QuoteJob(RUNNING, List.copyOf(metricValuesResult), null));
            }
            jobs.put(jobId, new QuoteJob(DONE, List.copyOf(metricValuesResult), null));
        } catch (RuntimeException e) {
            jobs.put(jobId, new QuoteJob(FAILED, List.copyOf(metricValuesResult), e.toString()));
        }
    }

    public QuoteJob find(UUID jobId) {
        return jobs.getIfPresent(jobId);
    }
}
