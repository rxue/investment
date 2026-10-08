package io.github.rxue.investment.api;

import io.github.rxue.investment.marketquote.QuoteMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/marketquote-jobs")
public class MarketQuoteJobController {
    private final QuoteService quoteService;
    public MarketQuoteJobController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping(consumes = "text/csv")
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
                .location(URI.create("/marketquote-jobs/" + jobId))
                .body(jobId);
    }

    @GetMapping("/{jobId}")
    public QuoteJob getJob(@PathVariable("jobId") UUID jobId) {
        QuoteJob job = quoteService.findJob(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found or expired");
        }
        return job;
    }
}
