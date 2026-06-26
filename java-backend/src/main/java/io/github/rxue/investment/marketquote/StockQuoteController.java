package io.github.rxue.investment.marketquote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/price")
public class StockQuoteController {
    private final YahooFinanceFetcher yahooFinanceFetcher;
    private final EuroPriceFetcher euroPriceFetcher;
    public StockQuoteController(YahooFinanceFetcher yahooFinanceFetcher, EuroPriceFetcher euroPriceFetcher) {
        this.yahooFinanceFetcher = yahooFinanceFetcher;
        this.euroPriceFetcher = euroPriceFetcher;
    }

    @GetMapping("/{companyIdentifier}")
    public Price getPrice(@PathVariable String companySymbol) throws IOException {
        return yahooFinanceFetcher.getCurrentPrice(companySymbol);
    }
    @GetMapping("/euro/{companyIdentifier}")
    public Price getEuroPrice(@PathVariable String companySymbol) {
        return euroPriceFetcher.getCurrentEuroPrice(companySymbol);
    }
}
