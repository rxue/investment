package io.github.rxue.investment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {

    @GetMapping("/result")
    public String getResult() {
        return "result";
    }
}

