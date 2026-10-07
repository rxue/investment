package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;


@RestController
public class MarketQuoteController {
    private final Repository marketQuoteRepository;
    public MarketQuoteController(Repository marketQuoteRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
    }

    @GetMapping("/marketquote/{securityId}")
    public Map<QuoteMetric,Comparable<?>> getMetricValues(@PathVariable("securityId") String securityId,
                                                          @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        return marketQuoteRepository.findMetricValues(securityId, metrics);
    }
}
