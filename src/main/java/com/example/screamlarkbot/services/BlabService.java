package com.example.screamlarkbot.services;

import com.example.screamlarkbot.models.blab.BlabRequest;
import com.example.screamlarkbot.models.blab.BlabResponse;
import com.example.screamlarkbot.models.blab.BlabStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlabService {

    private static final String URL = "https://zeapi.yandex.net/lab/api/yalm/text3";
    private final RestTemplate restTemplate;

    @Async
    @Retryable(value = Exception.class, maxAttempts = 10)
    public CompletableFuture<String> generate(BlabStyle style, String query) {
        log.info("blab is working...");
        if (query.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }
        BlabRequest request = BlabRequest.builder()
                .intro(style.getIntro())
                .query(query)
                .build();

        ResponseEntity<BlabResponse> response = restTemplate.postForEntity(URL, request, BlabResponse.class);
        if (!response.hasBody()) {
            log.info("response body is empty");
            return CompletableFuture.completedFuture(null);
        }
        BlabResponse body = response.getBody();
        if (body.getText() == null || body.getText().isBlank()) {
            log.info("response text is empty");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(body.getQuery() + body.getText());
    }

}
