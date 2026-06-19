package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRJob;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
class XIRRCalculationService {
    private final JobRepository jobRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final XIRRCalculator.Builder xirrCalculatorBuilder;

    public XIRRCalculationService(JobRepository jobRepository, ThreadPoolTaskExecutor taskExecutor, XIRRCalculator.Builder xirrCalculatorBuilder) {
        this.jobRepository = jobRepository;
        this.taskExecutor = taskExecutor;
        this.xirrCalculatorBuilder = xirrCalculatorBuilder;
    }

    public Long calculate(List<MultipartFile> files) {
        XIRRJob persistedJob = jobRepository.save(new XIRRJob());
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
        xirrCalculatorBuilder
                .setJob(persistedJob)
                .setUploadedFiles(uploadedFiles);
        taskExecutor.execute(xirrCalculatorBuilder.build());
        return persistedJob.getId();
    }
}
