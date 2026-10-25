package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

// прокси /api/data -> бэкенд по сети докера
@RestController
@RequestMapping("/api")
public class BackendProxyController {

    private static final MediaType TEXT_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final RestClient backend;

    public BackendProxyController(@Value("${BACKEND_URL:http://localhost:8080}") String backendUrl) {
        this.backend = RestClient.builder().baseUrl(backendUrl).build();
    }

    @PostMapping(value = "/data", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> sendData(@RequestBody(required = false) String data) {
        try {
            String result = backend.post()
                    .uri("/data")
                    .contentType(TEXT_UTF8)
                    .body(data == null ? "" : data)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(result);
        } catch (RestClientException e) {
            return ResponseEntity.status(502).body("Бэкенд недоступен: " + e.getMessage());
        }
    }

    @GetMapping(value = "/data", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> getData() {
        try {
            String result = backend.get()
                    .uri("/data")
                    .accept(TEXT_UTF8)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(result == null ? "" : result);
        } catch (RestClientException e) {
            return ResponseEntity.status(502).body("Бэкенд недоступен: " + e.getMessage());
        }
    }
}
