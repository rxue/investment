package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.metric.MetricValues;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static io.github.rxue.investment.marketquote.DerivedQuoteMetric.LATEST_PRICE;


@RestController
public class MarketQuoteController {
    private final Repository marketQuoteRepository;
    public MarketQuoteController(Repository marketQuoteRepository) {
        this.marketQuoteRepository = marketQuoteRepository;
    }

    @GetMapping("/marketquote/{securityId}")
    public MetricValues getLatestPrice(@PathVariable("securityId") String securityId) {
        List<MetricValues> metricValues = marketQuoteRepository.getMetricValues(List.of(securityId), Set.of(LATEST_PRICE));
        return metricValues.get(0);
    }
}
