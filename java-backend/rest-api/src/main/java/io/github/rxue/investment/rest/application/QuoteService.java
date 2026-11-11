package io.github.rxue.investment.rest.application;

import io.github.rxue.investment.rest.infrastructure.WatchListRepository;
import io.github.rxue.investment.rest.infrastructure.persistence.WatchList;
import io.github.rxue.investmentutility.marketquote.BatchQuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuoteService implements BatchQuoteFetcher {
    private final QuoteFetcher marketQuoteFetcher;
    private final WatchListRepository watchListRepository;
    public QuoteService(QuoteFetcher marketQuoteFetcher, WatchListRepository watchListRepository) {
        this.marketQuoteFetcher = marketQuoteFetcher;
        this.watchListRepository = watchListRepository;
    }
    @Override
    public List<MetricValues> fetchAll(SequencedSet<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        return marketQuoteFetcher.fetchAll(securityIds, quoteMetrics);
    }

    public List<MetricValues> fetchAllByWatchList(Long watchListId) throws NoSuchElementException {
        WatchList watchList = watchListRepository.findById(watchListId)
                .orElseThrow(() -> new NoSuchElementException("Watch List with id " + watchListId + " not found"));
        SequencedSet<String> securityIds = new LinkedHashSet<>(watchList.getSecurityIds());
        SequencedSet<QuoteMetric> quoteMetrics = new LinkedHashSet<>(watchList.getQuoteMetrics());
        return fetchAll(securityIds, quoteMetrics);
    }



}
