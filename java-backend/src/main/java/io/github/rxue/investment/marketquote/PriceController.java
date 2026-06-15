package io.github.rxue.investment.marketquote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PriceController {
    private final YahooFinanceFetcher yahooFinanceFetcher;
    public PriceController(YahooFinanceFetcher yahooFinanceFetcher) {
        this.yahooFinanceFetcher = yahooFinanceFetcher;
    }
    @GetMapping("/price/{companySymbol}")
    public Price getPrice(@PathVariable String companySymbol) throws IOException {
        return yahooFinanceFetcher.getCurrentPrice(companySymbol);
    }
}
