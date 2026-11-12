package io.github.rxue.investment.interfaces;

import io.github.rxue.investment.application.NotFoundException;
import io.github.rxue.investment.application.QuoteService;
import io.github.rxue.investment.application.WatchListService;
import io.github.rxue.investment.infrastructure.persistence.WatchList;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/watchlist")
public class WatchListController {
    private final WatchListService watchListService;
    private final QuoteService quoteService;
    public WatchListController(WatchListService watchListService, QuoteService quoteService) {
        this.watchListService = watchListService;
        this.quoteService = quoteService;
    }

    @GetMapping("/{id}")
    public WatchList getWatchList(@PathVariable("id") Long id) {
        return watchListService.findWatchList(id);
    }
    @GetMapping("/{id}/marketquotes")
    public List<MetricValues> getMetricValuesByWatchList(@PathVariable("id") Long watchListId) {
        return quoteService.fetchAllByWatchList(watchListId);
    }
}
