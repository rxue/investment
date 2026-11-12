package io.github.rxue.investment.application;

import io.github.rxue.investment.infrastructure.persistence.WatchList;
import io.github.rxue.investmentutility.marketquote.BatchQuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuoteService implements BatchQuoteFetcher {
    private final QuoteFetcher marketQuoteFetcher;
    private final WatchListService watchListService;
    public QuoteService(QuoteFetcher marketQuoteFetcher, WatchListService watchListService) {
        this.marketQuoteFetcher = marketQuoteFetcher;
        this.watchListService = watchListService;
    }
    @Override
    public List<MetricValues> fetchAll(SequencedSet<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        try {
            return marketQuoteFetcher.fetchAll(securityIds, quoteMetrics);
        } catch(QuoteFetchException e) {
            if (e.statusCode().isPresent() && e.statusCode().getAsInt() == HttpStatus.NOT_FOUND.value()) {
                throw new NotFoundException("some of the security ids from " + securityIds + " not found");
            } else throw e;
        }
    }

    public List<MetricValues> fetchAllByWatchList(Long watchListId) throws NotFoundException {
        WatchList watchList = watchListService.findWatchList(watchListId);
        SequencedSet<String> securityIds = new LinkedHashSet<>(watchList.getSecurityIds());
        SequencedSet<QuoteMetric> quoteMetrics = new LinkedHashSet<>(watchList.getQuoteMetrics());
        return fetchAll(securityIds, quoteMetrics);
    }

}
