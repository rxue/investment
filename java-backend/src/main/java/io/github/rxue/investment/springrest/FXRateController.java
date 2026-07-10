package io.github.rxue.investment.springrest;

import io.github.rxue.investment.marketquote.FXRateFetcher;
import io.github.rxue.investment.marketquote.Price;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@RestController
class FXRateController {
    private final FXRateFetcher fxRateFetcher;

    public FXRateController() {
        this.fxRateFetcher = new FXRateFetcher();
    }

    @GetMapping("/fxrate/euro/{currency}")
    public ResponseEntity<Price> getFxRateToEuro(@PathVariable String currency, @RequestParam(required = false) LocalDate date) {
        LocalDate rateDate = date == null ? LocalDate.now() : date;
        BigDecimal rate = fxRateFetcher.fetchFXRateToEuro(currency, rateDate);
        Price price = new Price(currency, rate, rateDate.atStartOfDay(ZonedDateTime.now().getZone()));
        return ResponseEntity.ok(price);
    }
}
