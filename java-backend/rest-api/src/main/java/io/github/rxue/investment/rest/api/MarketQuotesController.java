package io.github.rxue.investment.rest.api;

import io.github.rxue.investment.rest.application.QuoteService;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.QuotePrice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;



@RestController
@RequestMapping("/marketquotes")
public class MarketQuotesController {
    private final QuoteService quoteService;
    public MarketQuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
    @GetMapping()
    public List<MetricValuesDTO> getMetricValues(@RequestParam("securityIds") Set<String> securityIds,
                                                 @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        return quoteService.getQuoteMetricValuesList(securityIds, metrics).fullMap()
                .entrySet().stream()
                .map(MarketQuotesController::toMetricValuesDTO)
                .toList();
    }
    private static MetricValuesDTO toMetricValuesDTO(Map.Entry<String,Map<Metric,Comparable<?>>> entry) {
        return new MetricValuesDTO(entry.getKey(), entry.getValue());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String detail = "invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        if (e.getRequiredType() == QuoteMetric.class) {
            detail += "; allowed values: " + Arrays.toString(QuoteMetric.values());
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(QuoteFetchException.class)
    public ProblemDetail handleQuoteFetchException(QuoteFetchException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.resolve(e.statusCode().getAsInt()), e.getMessage());
    }
}
