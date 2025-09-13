package com.resilience4j.example.controller;

import com.resilience4j.example.service.Resilience4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("resilience4j")
public class Resilience4jController {

    @Autowired
    Resilience4jService resillience4jService;

    @GetMapping("/test")
    public Mono<String> get() {
        return resillience4jService.circuitBreakerTest();
    }
}
