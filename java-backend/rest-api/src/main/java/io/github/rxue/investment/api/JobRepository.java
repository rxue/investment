package io.github.rxue.investment.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.*;

public class JobRepository {
    private final Cache<UUID,MetricValuesList> jobs = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(1_000)
            .build();
    private final Repository marketQuoteRepository;

    public JobRepository(Repository marketQuoteRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
    }
    @Async
    public void query(UUID jobId, Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<MetricValues> metricValuesResult = new ArrayList<>();
        for (String securityId : securityIds) {
            Map<QuoteMetric,Comparable<?>> metricValues = marketQuoteRepository.findMetricValues(securityId, quoteMetrics);
            metricValuesResult.add(new MetricValues(securityId, Collections.unmodifiableMap(metricValues)));
            jobs.put(jobId, new MetricValuesList(List.copyOf(metricValuesResult)));
        }
    }

    public MetricValuesList find(UUID jobId) {
        return jobs.getIfPresent(jobId);
    }
}
