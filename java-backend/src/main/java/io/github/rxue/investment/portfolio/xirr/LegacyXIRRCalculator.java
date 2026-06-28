package io.github.rxue.investment.portfolio.xirr;

class LegacyXIRRCalculator {

/*    private final RawInputGenerator rawInputGenerator;
    private final OPTransactionExtractor transactionExtractor;
    private final XIRRJob job;
    private final List<Path> uploadedFiles;
    private final JobRepository jobRepository;
    private LegacyXIRRCalculator(Builder builder) {
        this.rawInputGenerator = builder.rawInputGenerator;
        this.transactionExtractor = builder.transactionExtractor;
        this.jobRepository = builder.jobRepository;
        this.job = builder.job;
        this.uploadedFiles = builder.uploadedFiles;
    }

    @Override
    public void run() {
*//*        List<OPTransaction> transactions = transactionExtractor.extract(uploadedFiles);
        XIRRRawInput rawInput = rawInputGenerator.generate(job, transactions);
        List<CashFlowInput> cashFlowInput = toCashFlowInput(rawInput);
        job.setResult(BigDecimal.valueOf(calculateXirr(cashFlowInput)));
        jobRepository.save(job);*//*
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


    static List<CashFlowInput> toCashFlowInput(XIRRRawInput rawInput) {
        List<CashFlowInput> cashFlows = new ArrayList<>();
        for (CashFlow cashFlow : rawInput.getCashFlows()) {
            cashFlows.add(CashFlowInput.toInput(cashFlow));
        }
        long marketValuesInEuroCent = rawInput.getHoldings().stream()
                .mapToLong(XIRRPosition::getEuroCentMarketValue)
                .sum();
        cashFlows.add(new CashFlowInput(LocalDate.now(), CashFlowType.ASSUMED_LIQUATION, marketValuesInEuroCent + rawInput.getCashInEuroCent()));
        return cashFlows;
    }
    @Service
    @Scope("prototype")
    static class Builder {
        private final RawInputGenerator rawInputGenerator;
        private final TransactionExtractor transactionExtractor;
        private final JobRepository jobRepository;
        private XIRRJob job;
        private List<Path> uploadedFiles;

        public Builder(RawInputGenerator rawInputGenerator, TransactionExtractor transactionExtractor, JobRepository jobRepository) {
            this.rawInputGenerator = rawInputGenerator;
            this.transactionExtractor = transactionExtractor;
            this.jobRepository = jobRepository;
        }

        public Builder setJob(XIRRJob job) {
            this.job = job;
            return this;
        }

        public Builder setUploadedFiles(List<Path> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
            return this;
        }

        LegacyXIRRCalculator build() {
            return new LegacyXIRRCalculator(this);
        }
    }*/
}
