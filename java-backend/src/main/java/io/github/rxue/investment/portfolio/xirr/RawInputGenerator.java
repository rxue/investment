package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.OPTransaction;
import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.portfolio.holdings.Company;
import io.github.rxue.investment.portfolio.holdings.CompanyRepository;
import io.github.rxue.investment.portfolio.xirr.jpaentity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
class RawInputGenerator {
    private final CompanyRepository companyRepository;
    private final LotsMatcher lotsMatcher;
    private final EuroPriceFetcher euroPriceFetcher;
    RawInputGenerator(CompanyRepository companyRepository, LotsMatcher lotsMatcher, EuroPriceFetcher euroPriceFetcher) {
        this.companyRepository = companyRepository;
        this.lotsMatcher = lotsMatcher;
        this.euroPriceFetcher = euroPriceFetcher;
    }
    XIRRRawInput generate(List<OPTransaction> transactions) {
        XIRRRawInput rawInput = initializeRawInputWithHoldings(transactions);
        setHoldingsMarketValues(rawInput);
        setCashFlows(rawInput, transactions);
        return rawInput;
    }

    private XIRRRawInput initializeRawInputWithHoldings(List<OPTransaction> transactions) {
        Map<String,MatchResult> matchResultByCompanySymbol = matchLots(transactions);
        XIRRRawInput rawInput = new XIRRRawInput();
        List<Position> holdings = matchResultByCompanySymbol.entrySet().stream()
                .map(entry -> toPosition(rawInput, entry))
                .toList();
        rawInput.setHoldings(holdings);
        setCashFlows(rawInput, transactions);
        setRemainingCash(rawInput, transactions);
        return rawInput;
    }
    private void setHoldingsMarketValues(XIRRRawInput rawInput) {
        for (Position position : rawInput.getHoldings()) {
            String yahooCompanySymbol = position.getCompany().getYahooSymbol();
            BigDecimal euroPrice = euroPriceFetcher.getCurrentEuroPrice(yahooCompanySymbol)
                    .value();
            Long euroCentMarketValue = euroPrice
                    .multiply(BigDecimal.valueOf(position.getShareAmount()))
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();
            position.setEuroCentMarketValue(euroCentMarketValue);
        }
    }
    private void setCashFlows(XIRRRawInput rawInput, List<OPTransaction> transactions) {
        List<CashFlow> cashFlows = transactions.stream()
                .filter(tr -> tr.category() == 710 && "TILISIIRTO".equals(tr.explanation()))
                .map(tr -> new CashFlow(rawInput, tr.effectiveDate(), toEuroCent(tr.amountInEuro())))
                .toList();
        rawInput.setCashFlows(cashFlows);
    }
    private void setRemainingCash(XIRRRawInput rawInput, List<OPTransaction> transactions) {
        long remainingCash = transactions.stream()
                .mapToLong(tr -> toEuroCent(tr.amountInEuro()))
                .sum();
        rawInput.setCashInEuroCent(remainingCash);
    }

    private Position toPosition(XIRRRawInput rawInput, Map.Entry<String,MatchResult> companySymbolToMatchResult) {
        String companySymbol = companySymbolToMatchResult.getKey();
        Company company = companyRepository.findByOpSymbol(companySymbol)
                .orElseThrow(() -> new IllegalArgumentException("company with symbol " + companySymbol + " cannot be found"));
        return new Position(company, companySymbolToMatchResult.getValue().unrealized().shareAmount(), rawInput);
    }

    private Map<String, MatchResult> matchLots(List<OPTransaction> tradingTransactions) {
        Map<String,List<Lot>> lotsBySymbol = Lot.toLotsByCompanySymbol(tradingTransactions);
        Map<String,MatchResult> matchLotsByCompanySymbol = new HashMap<>();
        for (Map.Entry<String,List<Lot>> entry : lotsBySymbol.entrySet()) {
            matchLotsByCompanySymbol.put(entry.getKey(), lotsMatcher.matchInFifo(entry.getValue(), List.of()));
        }
        return Collections.unmodifiableMap(matchLotsByCompanySymbol);
    }

    private static long toEuroCent(double euroValue) {
        return Math.round(euroValue * 100);
    }
}
