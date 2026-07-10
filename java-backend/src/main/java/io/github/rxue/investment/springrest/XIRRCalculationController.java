package io.github.rxue.investment.springrest;

import io.github.rxue.investment.portfolio.xirr.XIRRResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(XIRRCalculationController.IRR)
class XIRRCalculationController {

    private final XIRRCalculationService xirrCalculationService;
    public static final String IRR = "/irr";
    public XIRRCalculationController(XIRRCalculationService irrCalculationService) {
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
    public ResponseEntity<XIRRResult> getJob(@PathVariable("jobId") long jobId) {
        try {
            Optional<XIRRResult> result = xirrCalculationService.getResult(jobId);
            if (result.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            } else {
                return ResponseEntity.ok(result.get());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
/*
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

