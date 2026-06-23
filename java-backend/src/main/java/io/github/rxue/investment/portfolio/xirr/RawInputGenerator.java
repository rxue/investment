package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.OPTransaction;
import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.portfolio.holdings.jpaentity.Company;
import io.github.rxue.investment.portfolio.holdings.CompanyRepository;
import io.github.rxue.investment.portfolio.holdings.HoldingsGenerator;
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
    @PersistRawInput
    XIRRRawInput generate(XIRRJob job, List<OPTransaction> transactions) {
        XIRRRawInput rawInput = initializeRawInputWithHoldings(job, transactions);
        setHoldingsMarketValues(rawInput);
        setCashFlows(rawInput, transactions);
        return rawInput;
    }

    private XIRRRawInput initializeRawInputWithHoldings(XIRRJob job, List<OPTransaction> transactions) {
        Map<String,List<Lot>> lotsBySymbol = HoldingsGenerator.getTradingLotsByCompanySymbol(transactions);
        Map<String,MatchResult> matchResultByCompanySymbol = HoldingsGenerator.matchLots(lotsMatcher, lotsBySymbol);
        XIRRRawInput rawInput = new XIRRRawInput();
        rawInput.setJob(job);
        List<XIRRPosition> holdings = matchResultByCompanySymbol.entrySet().stream()
                .map(entry -> toPosition(rawInput, entry))
                .toList();
        rawInput.setHoldings(holdings);
        setCashFlows(rawInput, transactions);
        setRemainingCash(rawInput, transactions);
        return rawInput;
    }
    private void setHoldingsMarketValues(XIRRRawInput rawInput) {
        for (XIRRPosition position : rawInput.getHoldings()) {
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

    private XIRRPosition toPosition(XIRRRawInput rawInput, Map.Entry<String,MatchResult> companySymbolToMatchResult) {
        String companySymbol = companySymbolToMatchResult.getKey();
        Company company = companyRepository.findByOpSymbol(companySymbol)
                .orElseThrow(() -> new IllegalArgumentException("company with symbol " + companySymbol + " cannot be found"));
        return new XIRRPosition(company, companySymbolToMatchResult.getValue().unrealized().shareAmount(), rawInput);
    }

    private static long toEuroCent(double euroValue) {
        return Math.round(euroValue * 100);
    }
}
