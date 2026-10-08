package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.MetricValuesList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
public class MarketQuoteController {
    private final QuoteService quoteService;
    public MarketQuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/marketquote/{securityId}")
    public Map<QuoteMetric,Comparable<?>> getMetricValues(@PathVariable("securityId") String securityId,
                                                          @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        return quoteService.getQuote(securityId, metrics);
    }
    @PostMapping(path = "/marketquote", consumes = "text/csv")
    public ResponseEntity<UUID> createQueryJob(@RequestBody String csv,
                                               @RequestParam(name = "metrics") Set<QuoteMetric> metrics) {
        Set<String> securityIds = Arrays.stream(csv.split("[,\\r\\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (securityIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no ticker symbols given");
        }
        UUID jobId = quoteService.createQueryJob(securityIds, metrics);
        return ResponseEntity.accepted()
                .location(URI.create("/marketquote/jobs/" + jobId))
                .body(jobId);
    }
    @GetMapping("/marketquote/job/{id}")
    public MetricValuesList getMetricValues(@PathVariable("id") UUID jobId) {
        return quoteService.findMetricValues(jobId);
    }
}
