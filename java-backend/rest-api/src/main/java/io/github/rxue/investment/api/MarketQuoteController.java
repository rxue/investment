package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.QuoteMetric;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/marketquotes")
public class MarketQuoteController {
    private final QuoteService quoteService;
    public MarketQuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/{securityId}")
    public Map<QuoteMetric,Comparable<?>> getMetricValues(@PathVariable("securityId") String securityId,
                                                          @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        return quoteService.getQuote(securityId, metrics);
    }
}
