package io.github.rxue.investment.springrest;

import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping(XIRRCalculationController.IRR)
class XIRRCalculationController {
/*

    public static final String IRR = "/irr";
    private final JobRepository jobRepository;
    private final RawInputRepository rawInputRepository;
    private final XIRRCalculationService xirrCalculationService;
    public XIRRCalculationController(JobRepository jobRepository, RawInputRepository rawInputRepository, XIRRCalculationService irrCalculationService) {
        this.jobRepository = jobRepository;
        this.rawInputRepository = rawInputRepository;
        this.xirrCalculationService = irrCalculationService;
    }

    @PostMapping
    public ResponseEntity<Void> calculate(@RequestParam("file") List<MultipartFile> files) {
        long jobId = xirrCalculationService.calculate(files);
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
        return xirrCalculationService.getRawInput(jobId);
    }
    @GetMapping("/{jobId}/input")
    public List<CashFlowInput> getCashFlowInputList(@PathVariable("jobId") long jobId) {
        return xirrCalculationService.getCashFlowInputList(jobId);
    }
*/


}

