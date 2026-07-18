package io.github.rxue.investment.springrest;

import io.github.rxue.investment.marketquote.PriceFetcher;
import io.github.rxue.investment.marketquote.Price;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/price")
class StockQuoteController {
    //private final YahooFinanceFetcher yahooFinanceFetcher;
    private final PriceFetcher priceFetcher;
    public StockQuoteController() {
        //this.yahooFinanceFetcher = new YahooFinanceFetcher();
        this.priceFetcher = new PriceFetcher();
    }

    @GetMapping("/{companyIdentifier}")
    public Price getPrice(@PathVariable String companySymbol) throws IOException {
        //return yahooFinanceFetcher.getCurrentPrice(companySymbol);
        throw new UnsupportedOperationException();
    }
    @GetMapping("/euro/{companyIdentifier}")
    public Price getEuroPrice(@PathVariable String companySymbol) {
        return priceFetcher.getCurrentPriceInEuro(companySymbol);
    }
}
