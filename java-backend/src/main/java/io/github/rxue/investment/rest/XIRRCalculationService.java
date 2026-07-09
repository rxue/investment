package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRJob;
import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRRawInput;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
class XIRRCalculationService {
    //private final JobRepository jobRepository;
    //private final RawInputRepository rawInputRepository;
    //private final ThreadPoolTaskExecutor taskExecutor;
    //private final ObjectProvider<LegacyXIRRCalculator.Builder> xirrCalculatorBuilderProvider;
/*
    public XIRRCalculationService(JobRepository jobRepository, RawInputRepository rawInputRepository, ThreadPoolTaskExecutor taskExecutor, ObjectProvider<LegacyXIRRCalculator.Builder> xirrCalculatorBuilderProvider) {
        this.jobRepository = jobRepository;
        this.rawInputRepository = rawInputRepository;
        this.taskExecutor = taskExecutor;
        this.xirrCalculatorBuilderProvider = xirrCalculatorBuilderProvider;
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
        LegacyXIRRCalculator.Builder xirrCalculatorBuilder = xirrCalculatorBuilderProvider.getObject()
                .setJob(persistedJob)
                .setUploadedFiles(uploadedFiles);
        taskExecutor.execute(xirrCalculatorBuilder.build());
        return persistedJob.getId();
    }
    public XIRRRawInput getRawInput(long jobId) {
        return rawInputRepository.findAll().stream()
                .filter(rawInput -> rawInput.getJob().getId() == jobId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Raw input for job with id " + jobId + " doesnot exist"));
    }
    List<CashFlowInput> getCashFlowInputList(@PathVariable("jobId") long jobId) {
        return LegacyXIRRCalculator.toCashFlowInput(getRawInput(jobId));
    }*/
}
