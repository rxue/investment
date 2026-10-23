package io.github.rxue.investment.api;

import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class QuoteService {
    private final QuoteFetcher marketQuoteRepository;
    private final JobRepository jobRepository;

    public QuoteService(QuoteFetcher marketQuoteRepository, JobRepository jobRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
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
    public Map<QuoteMetric,Comparable<?>> getQuote(String securityId, Set<QuoteMetric> quoteMetrics) {
        return marketQuoteRepository.fetch(securityId, quoteMetrics);
    }

}
