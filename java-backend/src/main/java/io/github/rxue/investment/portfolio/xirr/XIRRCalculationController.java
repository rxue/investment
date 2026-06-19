package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRJob;
import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRRawInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(XIRRCalculationController.IRR)
class XIRRCalculationController {

    public static final String IRR = "/irr";
    private final JobRepository jobRepository;
    private final RawInputRepository rawInputRepository;
    private final XIRRCalculationService irrCalculationService;
    public XIRRCalculationController(JobRepository jobRepository, RawInputRepository rawInputRepository, XIRRCalculationService irrCalculationService) {
        this.jobRepository = jobRepository;
        this.rawInputRepository = rawInputRepository;
        this.irrCalculationService = irrCalculationService;
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
    public XIRRJob getJob(@PathVariable("jobId") long jobId) {
        return jobRepository.findById(jobId).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("/{jobId}/rawinput")
    public XIRRRawInput getRawInput(@PathVariable("jobId") long jobId) {
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("job with id " + jobId + " doesnot exist"));
        return rawInputRepository.findAll().get(0);
    }


}

