package io.github.rxue.investment.interfaces.web;

import io.github.rxue.investment.application.BenchmarkingService;
import io.github.rxue.investment.domain.benchmark.BenchmarkComparison;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {
    private final BenchmarkingService benchmarkingService;
    public BenchmarkController(BenchmarkingService benchmarkingService) {
        this.benchmarkingService = benchmarkingService;
    }

    @GetMapping()
    public BenchmarkComparison getBenchmarketReturns(@RequestParam("benchmarkSecurityId") String benchmarkSecurityId, @RequestParam("securityId") List<String> securityIds,
                                                     @RequestParam("inclusiveStartDate") LocalDate inclusiveStartDate,
                                                     @RequestParam("inclusiveEndDate") LocalDate inclusiveEndDate) {
        InclusiveDateRange dateRange = new InclusiveDateRange(inclusiveStartDate, inclusiveEndDate);

        return benchmarkingService.calculate(benchmarkSecurityId, dateRange, securityIds);
    }
}
