package io.github.rxue.investment.rest.application;

import io.github.rxue.investment.rest.api.JobRepository;
import io.github.rxue.investment.rest.api.QuoteJob;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValuesList;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuoteService {
    private final QuoteFetcher marketQuoteFetcher;
    private final JobRepository jobRepository;

    public QuoteService(QuoteFetcher marketQuoteFetcher, JobRepository jobRepository) {
        this.marketQuoteFetcher = marketQuoteFetcher;
        this.jobRepository = jobRepository;
    }
    public UUID createQueryJob(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        UUID jobId = jobRepository.create();
        jobRepository.query(jobId, securityIds, quoteMetrics);
        return jobId;
    }
    public QuoteJob findJob(UUID jobId) {
        return jobRepository.find(jobId);
    }

    public MetricValuesList getQuoteMetricValuesList(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        return marketQuoteFetcher.fetchAll(securityIds, quoteMetrics);
    }

}
