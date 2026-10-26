package io.github.rxue.investment.rest.api;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/marketquotes")
public class MarketQuotesController {
    private final QuoteService quoteService;
    public MarketQuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/{securityId}")
    public Map<QuoteMetric,Comparable<?>> getMetricValues(@PathVariable("securityId") String securityId,
                                                          @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        return quoteService.getQuote(securityId, metrics);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String detail = "invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        if (e.getRequiredType() == QuoteMetric.class) {
            detail += "; allowed values: " + Arrays.toString(QuoteMetric.values());
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
