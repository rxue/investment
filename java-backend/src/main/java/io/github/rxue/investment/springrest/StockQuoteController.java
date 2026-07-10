package io.github.rxue.investment.springrest;

import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.marketquote.Price;
import io.github.rxue.investment.marketquote.YahooFinanceFetcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/price")
class StockQuoteController {
    private final YahooFinanceFetcher yahooFinanceFetcher;
    private final EuroPriceFetcher euroPriceFetcher;
    public StockQuoteController() {
        this.yahooFinanceFetcher = new YahooFinanceFetcher();
        this.euroPriceFetcher = new EuroPriceFetcher();
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
