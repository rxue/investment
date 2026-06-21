package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.OPTransaction;
import io.github.rxue.investment.portfolio.xirr.jpaentity.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class XIRRCalculator implements Runnable {

    private final RawInputGenerator rawInputGenerator;
    private final XIRRJob job;
    private final List<Path> uploadedFiles;
    private final JobRepository jobRepository;
    private final RawInputRepository rawInputRepository;
    private XIRRCalculator(Builder builder) {
        this.rawInputGenerator = builder.rawInputGenerator;
        this.jobRepository = builder.jobRepository;
        this.rawInputRepository = builder.rawInputRepository;
        this.job = builder.job;
        this.uploadedFiles = builder.uploadedFiles;
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
        XIRRRawInput rawInput = rawInputGenerator.generate(transactions);
        rawInput.setJob(job);
        rawInputRepository.save(rawInput);
        List<CashFlowInput> cashFlowInput = toCashFlowInput(rawInput);
        job.setResult(BigDecimal.valueOf(calculateXirr(cashFlowInput)));
        jobRepository.save(job);
    }

    private static double calculateXirr(List<CashFlowInput> cashFlowInput) {
        List<Transaction> xirrTransactions = cashFlowInput.stream()
                .map(cf -> new Transaction(toSignedEuroAmount(cf), cf.getDate()))
                .toList();
        return new Xirr(xirrTransactions).xirr();
    }

    private static double toSignedEuroAmount(CashFlowInput cashFlowInput) {
        double amount = cashFlowInput.getValueInCent() / 100.0;
        return cashFlowInput.getType() == CashFlowType.DEPOSIT ? -amount : amount;
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

    static List<CashFlowInput> toCashFlowInput(XIRRRawInput rawInput) {
        List<CashFlowInput> cashFlows = new ArrayList<>();
        for (CashFlow cashFlow : rawInput.getCashFlows()) {
            cashFlows.add(CashFlowInput.toInput(cashFlow));
        }
        long marketValuesInEuroCent = rawInput.getHoldings().stream()
                .mapToLong(Position::getEuroCentMarketValue)
                .sum();
        cashFlows.add(new CashFlowInput(LocalDate.now(), CashFlowType.ASSUMED_LIQUATION, marketValuesInEuroCent + rawInput.getCashInEuroCent()));
        return cashFlows;
    }
    @Service
    @Scope("prototype")
    static class Builder {
        private final RawInputGenerator rawInputGenerator;
        private final JobRepository jobRepository;
        private final RawInputRepository rawInputRepository;
        private XIRRJob job;
        private List<Path> uploadedFiles;

        public Builder(RawInputGenerator rawInputGenerator, JobRepository jobRepository, RawInputRepository rawInputRepository) {
            this.rawInputGenerator = rawInputGenerator;
            this.jobRepository = jobRepository;
            this.rawInputRepository = rawInputRepository;
        }

        public Builder setJob(XIRRJob job) {
            this.job = job;
            return this;
        }

        public Builder setUploadedFiles(List<Path> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
            return this;
        }

        XIRRCalculator build() {
            return new XIRRCalculator(this);
        }
    }
}
