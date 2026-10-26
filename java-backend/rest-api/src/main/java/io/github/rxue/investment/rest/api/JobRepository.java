package io.github.rxue.investment.rest.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.*;

import static io.github.rxue.investment.rest.api.QuoteJob.Status.DONE;
import static io.github.rxue.investment.rest.api.QuoteJob.Status.FAILED;
import static io.github.rxue.investment.rest.api.QuoteJob.Status.RUNNING;

public class JobRepository {
    private final Cache<UUID,QuoteJob> jobs = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(1_000)
            .build();
    private final QuoteFetcher marketQuoteFetcher;

    public JobRepository(QuoteFetcher marketQuoteFetcher) {
        this.marketQuoteFetcher = marketQuoteFetcher;
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
                Map<QuoteMetric,Comparable<?>> metricValueMap = marketQuoteFetcher.fetch(securityId, quoteMetrics);
                MetricValues metricValues = new MetricValues.Builder(securityId)
                        .addAll(Collections.unmodifiableMap(metricValueMap)).build();
                metricValuesResult.add(metricValues);
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
