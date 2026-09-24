package io.github.rxue.investment.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketQuoteController {
    @GetMapping("/marketquote")
    public MarketQuote get() {
        return new MarketQuote("YAHOO");
    }
}
