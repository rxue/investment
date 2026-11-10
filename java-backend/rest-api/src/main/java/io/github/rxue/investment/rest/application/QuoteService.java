package io.github.rxue.investment.rest.application;

import io.github.rxue.investment.rest.infrastructure.WatchListRepository;
import io.github.rxue.investment.rest.infrastructure.persistence.WatchList;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuoteService {
    private final QuoteFetcher marketQuoteFetcher;
    private final WatchListRepository watchListRepository;
    public QuoteService(QuoteFetcher marketQuoteFetcher, WatchListRepository watchListRepository) {
        this.marketQuoteFetcher = marketQuoteFetcher;
        this.watchListRepository = watchListRepository;
    }

    public List<MetricValues> getQuoteMetricValuesList(Set<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        return marketQuoteFetcher.fetchAll(securityIds, quoteMetrics);
    }

    public List<MetricValues> getQuoteMetricValuesByWatchList(Long watchListId) throws WatchListNotFoundException {
        WatchList watchList = watchListRepository.findById(watchListId)
                .orElseThrow(() -> new WatchListNotFoundException("Watch List not found"));
        SequencedSet<String> securityIds = new LinkedHashSet<>(watchList.getSecurityIds());
        SequencedSet<QuoteMetric> quoteMetrics = new LinkedHashSet<>(watchList.getQuoteMetrics());
        return getQuoteMetricValuesList(securityIds, quoteMetrics);
    }



}
