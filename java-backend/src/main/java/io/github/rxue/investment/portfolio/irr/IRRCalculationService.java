package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.marketquote.YahooFinanceFetcher;
import io.github.rxue.investment.portfolio.TransactionFilter;
import io.github.rxue.investment.portfolio.holdings.CompanyRepository;
import io.github.rxue.investment.portfolio.irr.jpaentity.IRRJob;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
class IRRCalculationService {
    private final JobRepository jobRepository;
    private final TransactionFilter transactionFilter;
    private final LotsMatcher lotsMatcher;
    private final EuroPriceFetcher priceFetcher;
    private final CompanyRepository companyRepository;
    private final IRRResultRepository irrResultRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    public IRRCalculationService(JobRepository jobRepository,
                                 TransactionFilter transactionFilter,
                                 LotsMatcher lotsMatcher,
                                 EuroPriceFetcher priceFetcher,
                                 CompanyRepository companyRepository,
                                 IRRResultRepository irrResultRepository,
                                 ThreadPoolTaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.transactionFilter = transactionFilter;
        this.lotsMatcher = lotsMatcher;
        this.priceFetcher = priceFetcher;
        this.companyRepository = companyRepository;
        this.irrResultRepository = irrResultRepository;
        this.taskExecutor = taskExecutor;
    }

    public long calculate(List<MultipartFile> files) {
        IRRJob newJob = jobRepository.save(new IRRJob());
        List<Path> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            Path path = null;
            try {
                path = Files.createTempFile(file.getName(), null);
                file.transferTo(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            uploadedFiles.add(path);
        }
        taskExecutor.execute(
                new IRRCalculator(newJob, jobRepository, uploadedFiles, transactionFilter, lotsMatcher, priceFetcher, companyRepository, irrResultRepository));
        return newJob.getId();
    }
}
