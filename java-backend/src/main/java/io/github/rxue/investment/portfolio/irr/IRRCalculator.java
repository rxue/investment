package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.OPTransaction;
import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.marketquote.Price;
import io.github.rxue.investment.portfolio.TransactionFilter;
import io.github.rxue.investment.portfolio.holdings.Company;
import io.github.rxue.investment.portfolio.holdings.CompanyRepository;
import io.github.rxue.investment.portfolio.irr.jpaentity.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class IRRCalculator implements Runnable {
    private final IRRJob job;
    private final JobRepository jobRepository;
    private final List<Path> uploadedFiles;
    private final TransactionFilter transactionFilter;
    private final LotsMatcher lotsMatcher;
    private final EuroPriceFetcher stockPriceFetcher;
    private final CompanyRepository companyRepository;
    private final IRRResultRepository irrResultRepository;
    public IRRCalculator(IRRJob job,
                         JobRepository jobRepository,
                         List<Path> uploadedFiles,
                         TransactionFilter transactionFilter,
                         LotsMatcher lotsMatcher,
                         EuroPriceFetcher stockPriceFetcher,
                         CompanyRepository companyRepository,
                         IRRResultRepository irrResultRepository) {
        this.job = job;
        this.jobRepository = jobRepository;
        this.uploadedFiles = uploadedFiles;
        this.transactionFilter = transactionFilter;
        this.lotsMatcher = lotsMatcher;
        this.stockPriceFetcher = stockPriceFetcher;
        this.companyRepository = companyRepository;
        this.irrResultRepository = irrResultRepository;
    }

    @Override
    public void run() {
        List<OPTransaction> transactions = uploadedFiles.stream()
                .map(filePath -> {
                    try {
                        return extractTransactions(Files.newInputStream(filePath));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).flatMap(List::stream)
                .toList();
        IRRJob managedJob = updateJob(transactions);
        try {
            updatePositionMarketValues(managedJob);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IRRResult irrResult = new IRRResult();
        irrResult.setJob(managedJob);
        List<XIRRCashFlow> xirrCashFlows = generateXIRRCashFlows(irrResult);
        irrResult.setCashFlows(xirrCashFlows);
        irrResultRepository.save(irrResult);
        irrResult.setValue(calculateXirr(xirrCashFlows));
        irrResultRepository.save(irrResult);
    }
    private double calculateXirr(List<XIRRCashFlow> xirrCashFlows) {
        List<Transaction> transactions = xirrCashFlows.stream()
                .map(cf -> new Transaction(cf.getValueInCent() / 100.0, cf.getDate()))
                .toList();
        return new Xirr(transactions).xirr();
    }
    private IRRJob updateJob(List<OPTransaction> transactions) {
        job.setAssumedLiquidationDate(LocalDate.now());
        job.setHoldings(generateNewHoldings(transactions));
        job.setCashInCent(remainingCashInCent(transactions));
        job.setCashFlows(findDeposits(transactions));
        return jobRepository.save(job);
    }
    private void updatePositionMarketValues(IRRJob managedJob) throws IOException {
        for (Position position : managedJob.getHoldings()) {
            Price euroPrice = stockPriceFetcher.getCurrentEuroPrice(position.getCompany().getYahooSymbol());
            double marketValue = euroPrice.value().multiply(BigDecimal.valueOf(position.getShareAmount()))
                    .doubleValue();
            position.setEuroCentMarketValue(euroAmountToCent(marketValue));
            jobRepository.save(managedJob);
        }
    }
    private List<Position> generateNewHoldings(List<OPTransaction> transactions) {
        Map<String,MatchResult> matchResultsBySymbol = matchLots(transactionFilter.findTradings(transactions));
        matchResultsBySymbol.forEach((companySymbol, matchLot) -> System.out.println(companySymbol + " : " + matchLot));
        return matchResultsBySymbol.entrySet().stream()
                .map(this::toPosition)
                .toList();
    }
    private static long remainingCashInCent(List<OPTransaction> transactions) {
        return transactions.stream()
                .mapToLong(tr -> euroAmountToCent(tr))
                .sum();
    }

    private static long euroAmountToCent(OPTransaction tr) {
        return Math.round(tr.amountInEuro() * 100);
    }
    private static long euroAmountToCent(double amount) {
        return Math.round(amount * 100);
    }

    private Position toPosition(Map.Entry<String,MatchResult> companySymbolToMatchResult) {
        Position position = new Position(job);
        Company company = companyRepository.findByOpSymbol(companySymbolToMatchResult.getKey())
                .orElseThrow();
        position.setCompany(company);
        int shareAmount = companySymbolToMatchResult.getValue()
                .unrealized().lots().stream()
                .mapToInt(Lot.Buy::shareAmount)
                .sum();
        position.setShareAmount(shareAmount);
        return position;
    }
    private Map<String, MatchResult> matchLots(List<OPTransaction> tradingTransactions) {
        Map<String,List<Lot>> lotsBySymbol = Lot.toLotsByCompanySymbol(tradingTransactions);
        Map<String,MatchResult> matchLotsByCompanySymbol = new HashMap<>();
        for (Map.Entry<String,List<Lot>> entry : lotsBySymbol.entrySet()) {
            matchLotsByCompanySymbol.put(entry.getKey(), lotsMatcher.matchInFifo(entry.getValue(), List.of()));
        }
        return Collections.unmodifiableMap(matchLotsByCompanySymbol);
    }

    static List<OPTransaction> extractTransactions(InputStream inputStream) throws IOException {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final Charset encoding = Charset.forName("ISO-8859-1");
        final String fieldValueDate = "Arvopäivä";
        final String fieldAmount = "Määrä EUROA";
        final String fieldCategory = "Laji";
        final String fieldExplanation = "Selitys";
        final String fieldMessage = "Viesti";
        try (Reader reader = new InputStreamReader(inputStream, encoding);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {
            return parser.getRecords().stream()
                    .map(r -> new OPTransaction(
                            LocalDate.parse(r.get(fieldValueDate).trim(), dateFormat),
                            Double.parseDouble(r.get(fieldAmount).trim().replace("+", "").replace(",", ".")),
                            Integer.parseInt(r.get(fieldCategory).trim()),
                            r.get(fieldExplanation).trim(),
                            r.get(fieldMessage).trim()
                    ))
                    .toList();
        }
    }
    private List<CashFlow> findDeposits(List<OPTransaction> allTransactions) {
        return allTransactions.stream()
            .filter(tr -> tr.category() == 710 && "TILISIIRTO".equals(tr.explanation()))
            .map(tr -> new CashFlow(tr.effectiveDate(), euroAmountToCent(tr), job))
            .toList();
    }
    private List<XIRRCashFlow> generateXIRRCashFlows(IRRResult irrResult) {
        IRRJob managedJob = irrResult.getJob();
        List<XIRRCashFlow> xirrCashFlows = managedJob.getCashFlows().stream()
                .map(cf -> new XIRRCashFlow(false, cf.getDate(), cf.getAmountInCent(), irrResult))
                .toList();
        List<XIRRCashFlow> result = new ArrayList<>(xirrCashFlows);
        long stockMarketValue = managedJob.getHoldings().stream()
                .mapToLong(Position::getEuroCentMarketValue)
                .sum();
        result.add(new XIRRCashFlow(true, job.getAssumedLiquidationDate(), -(stockMarketValue + job.getCashInCent()), irrResult));
        return Collections.unmodifiableList(result);
    }
}
