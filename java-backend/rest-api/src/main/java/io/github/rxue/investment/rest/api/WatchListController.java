package io.github.rxue.investment.rest.api;

import io.github.rxue.investment.rest.application.QuoteService;
import io.github.rxue.investment.rest.application.WatchListService;
import io.github.rxue.investment.rest.infrastructure.persistence.WatchList;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        return quoteService.getQuoteMetricValuesByWatchList(watchListId);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleWatchListNotFoundException(NoSuchElementException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(QuoteFetchException.class)
    public ProblemDetail handleQuoteFetchException(QuoteFetchException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.resolve(e.statusCode().getAsInt()), e.getMessage());
    }
}
