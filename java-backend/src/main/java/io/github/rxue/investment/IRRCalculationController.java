package io.github.rxue.investment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@RestController
public class RequestController {

    @PostMapping("/irr")
    public ResponseEntity<Void> calculateIRR(@RequestParam("file") MultipartFile file) {
        String resultId = UUID.randomUUID().toString();
        return ResponseEntity.created(URI.create("/irr/" + resultId)).build();
    }
}

