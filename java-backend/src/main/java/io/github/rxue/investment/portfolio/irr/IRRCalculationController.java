package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.portfolio.irr.jpaentity.IRRJob;
import io.github.rxue.investment.portfolio.irr.jpaentity.IRRResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(IRRCalculationController.IRR)
class IRRCalculationController {

    public static final String IRR = "/irr";
    private final JobRepository jobRepository;
    private final IRRCalculationService irrCalculationService;
    private final IRRResultRepository irrResultRepository;
    public IRRCalculationController(JobRepository jobRepository, IRRCalculationService irrCalculationService, IRRResultRepository irrResultRepository) {
        this.jobRepository = jobRepository;
        this.irrCalculationService = irrCalculationService;
        this.irrResultRepository = irrResultRepository;
    }

    @PostMapping
    public ResponseEntity<Void> calculate(@RequestParam("file") List<MultipartFile> files) {
        long jobId = irrCalculationService.calculate(files);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .location(URI.create(IRR + "/" + jobId))
                .build();
    }
    @GetMapping("/{jobId}")
    public IRRJob getJob(@PathVariable("jobId") long jobId) {
        return jobRepository.findById(jobId).orElseThrow(IllegalArgumentException::new);
    }
    @GetMapping("/result/{jobId}")
    public IRRResult getResult(@PathVariable("jobId") long jobId) {
        Optional<IRRJob> jobOpt = jobRepository.findById(jobId);
        return irrResultRepository.findByJob(jobOpt.get())
                .orElseThrow(() -> new IllegalArgumentException("no result with job id " + jobId));
    }

}

