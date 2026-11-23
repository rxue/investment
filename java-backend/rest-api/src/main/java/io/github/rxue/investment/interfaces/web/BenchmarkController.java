package io.github.rxue.investment.interfaces.web;

import io.github.rxue.investment.application.CumulativeReturnService;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {
    private final CumulativeReturnService cumulativeReturnService;
    public BenchmarkController(CumulativeReturnService cumulativeReturnService) {
        this.cumulativeReturnService = cumulativeReturnService;
    }

    @GetMapping()
    public List<ReturnValue> getBenchmarketReturns(@RequestParam("securityId") String securityId, @RequestParam("benchmarkSecurityId") String benchmarkSecurityId,
                                                          @RequestParam("inclusiveStartDate") LocalDate inclusiveStartDate,
                                                          @RequestParam("inclusiveEndDate") LocalDate inclusiveEndDate) {
        InclusiveDateRange dateRange = new InclusiveDateRange(inclusiveStartDate, inclusiveEndDate);
        return cumulativeReturnService.calculate(securityId, dateRange);
    }
}
