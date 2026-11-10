package io.github.rxue.investment.rest.api;

import io.github.rxue.investment.rest.application.QuoteService;
import io.github.rxue.investment.rest.application.WatchListNotFoundException;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.SequencedSet;
import java.util.Set;



@RestController
@RequestMapping("/marketquotes")
public class MarketQuotesController {
    private final QuoteService quoteService;
    public MarketQuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
    @GetMapping()
    public List<MetricValues> getMetricValues(@RequestParam("securityIds") Set<String> securityIds,
                                              @RequestParam(name = "metrics") SequencedSet<QuoteMetric> metrics,
                                              @RequestHeader HttpHeaders httpHeaders) {
        return quoteService.getQuoteMetricValuesList(securityIds, metrics);
    }
    @GetMapping("/watchlist/{id}")
    public List<MetricValues> getMetricValuesByWatchList(@PathVariable("id") Long watchListId) throws WatchListNotFoundException {
        return quoteService.getQuoteMetricValuesByWatchList(watchListId);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String detail = "invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        if (e.getRequiredType() == QuoteMetric.class) {
            detail += "; allowed values: " + Arrays.toString(QuoteMetric.values());
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(WatchListNotFoundException.class)
    public ProblemDetail handleWatchListNotFoundException(WatchListNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(QuoteFetchException.class)
    public ProblemDetail handleQuoteFetchException(QuoteFetchException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.resolve(e.statusCode().getAsInt()), e.getMessage());
    }
}
