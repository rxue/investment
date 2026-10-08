package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.MetricValuesList;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class QuoteService {
    private final Repository marketQuoteRepository;
    private final JobRepository jobRepository;

    public QuoteService(Repository marketQuoteRepository, JobRepository jobRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
        this.jobRepository = jobRepository;
    }
    public UUID createQueryJob(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        UUID jobId = UUID.randomUUID();
        jobRepository.query(jobId, securityIds, quoteMetrics);
        return jobId;
    }
    public MetricValuesList findMetricValues(UUID jobId) {
        return jobRepository.find(jobId);
    }
    public Map<QuoteMetric,Comparable<?>> getQuote(String securityId, Set<QuoteMetric> quoteMetrics) {
        return marketQuoteRepository.findMetricValues(securityId, quoteMetrics);
    }

}
