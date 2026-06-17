package io.github.rxue.investment;

import io.github.rxue.investment.entity.Job;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.portfolio.TransactionFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(IRRCalculationController.IRR)
public class IRRCalculationController {

    public static final String IRR = "/irr";
    private final JobRepository jobRepository;
    private final TransactionFilter transactionFilter;
    private final LotsMatcher lotsMatcher;
    private final ThreadPoolTaskExecutor taskExecutor;

    public IRRCalculationController(JobRepository jobRepository,
                                    TransactionFilter transactionFilter,
                                    LotsMatcher lotsMatcher,
                                    ThreadPoolTaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.transactionFilter = transactionFilter;
        this.lotsMatcher = lotsMatcher;
        this.taskExecutor = taskExecutor;
    }

    @PostMapping
    public ResponseEntity<Void> calculateIRRRequest(@RequestParam("file") List<MultipartFile> files) {

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .location(URI.create(IRR + "/" + resultId))
                .build();
    }
}

