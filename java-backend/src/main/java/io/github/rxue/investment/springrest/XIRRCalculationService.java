package io.github.rxue.investment.springrest;

import io.github.rxue.investment.adapter.op.OPXIRRCalculator;
import io.github.rxue.investment.portfolio.xirr.XIRRResult;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
class XIRRCalculationService {
    private final OPXIRRCalculator opxirrCalculator;
    private final JobRepository jobRepository;
    private final AsyncTaskExecutor executor;

    public XIRRCalculationService(JobRepository jobRepository, AsyncTaskExecutor executor) {
        this.opxirrCalculator = new OPXIRRCalculator();
        this.jobRepository = jobRepository;
        this.executor = executor;
    }

    public long calculate(List<MultipartFile> files) {
        // Read eagerly on the request thread: the multipart parts backing
        // NOTE! MultipartFile.getInputStream() may be discarded once this request
        // completes, before the async calculation below gets to read them.
        List<InputStream> uploadedCSVFileInputStreams = files.stream()
                .map(mf -> {
                    try {
                        return (InputStream) new ByteArrayInputStream(mf.getBytes());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).toList();

        Future<XIRRResult> result = executor.submit(() -> opxirrCalculator.calculate(uploadedCSVFileInputStreams));
        Job job = Job.newJob(result);
        jobRepository.save(job);
        return job.id();
    }

    public Optional<XIRRResult> getResult(long jobId) throws ExecutionException, InterruptedException {
        Future<XIRRResult> futureResult = jobRepository.findById(jobId)
                .result();
        return futureResult.isDone() ? Optional.of(futureResult.get()) : Optional.empty();
    }
}
