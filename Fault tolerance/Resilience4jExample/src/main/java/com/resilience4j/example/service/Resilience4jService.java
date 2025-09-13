package com.resilience4j.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class Resilience4jService {

    private final WebClient webClient;

    public Resilience4jService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080").build();
    }

    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallback")
    public Mono<String> circuitBreakerTest() {
        return webClient.get()
                .uri("/redis/getUser/121")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("HTTP Error: " + errorBody))))
                .bodyToMono(String.class);
    }

    // fallback method must have same signature + Throwable as last arg
    public Mono<String> fallback(Throwable t) {
        // Here you can add logging if needed
        return Mono.just("Fallback response due to error: " + t.getMessage());
    }
}
