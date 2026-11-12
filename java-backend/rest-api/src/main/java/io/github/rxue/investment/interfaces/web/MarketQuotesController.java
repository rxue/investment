package io.github.rxue.investment.interfaces;

import io.github.rxue.investment.application.QuoteService;
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
@RequestMapping("/marketquotes")
public class MarketQuotesController {
    private final QuoteService quoteService;
    public MarketQuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
    @GetMapping()
    public List<MetricValues> getMetricValues(@RequestParam("securityIds") List<String> securityIds,
                                              @RequestParam(name = "metrics") SequencedSet<QuoteMetric> metrics,
                                              @RequestHeader HttpHeaders httpHeaders) {
        return quoteService.fetchAll(new LinkedHashSet<>(securityIds), metrics);
    }
}
