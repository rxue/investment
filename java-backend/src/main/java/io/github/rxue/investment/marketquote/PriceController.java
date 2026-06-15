package io.github.rxue.investment.marketquote;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    public ResponseEntity<Price> getPrice(@PathVariable String companySymbol, @CookieValue(required = false) String crumb) throws IOException {
        Pair<String, Price> crumbAndPrice = yahooFinanceFetcher.getCurrentPrice(companySymbol);
        ResponseCookie crumbCookie = ResponseCookie.from("crumb", crumbAndPrice.getFirst())
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, crumbCookie.toString())
                .body(crumbAndPrice.getSecond());
    }
}
